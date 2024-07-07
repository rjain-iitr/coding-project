package com.calendly.verticle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.calendly.model.Availability;
import com.calendly.repository.AvailabilityRepository;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class AvailabilityVerticle extends AbstractVerticle {

	private AvailabilityRepository availabilityRepository = new AvailabilityRepository();

	@Override
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.get("/users/:userId/availability").handler(this::handleGetAvailabilities);
		router.post("/users/:userId/availability").handler(this::handleCreateAvailability);
		router.put("/users/:userId/availability/:id").handler(this::handleUpdateAvailability);
		router.delete("/users/:userId/availability/:id").handler(this::handleDeleteAvailability);
		router.get("/users/:userId1/availability/overlap/:userId2").handler(this::handleGetOverlap);

		vertx.createHttpServer().requestHandler(router).listen(8080);
	}

	private void handleGetAvailabilities(RoutingContext routingContext) {
		String userId = routingContext.request().getParam("userId");
		List<Availability> availabilityList = availabilityRepository.findByUserId(userId);
		routingContext.response().putHeader("content-type", "application/json")
				.end(Json.encodePrettily(availabilityList));
	}

	private void handleCreateAvailability(RoutingContext routingContext) {
		String userId = routingContext.request().getParam("userId");
		Availability availability = Json.decodeValue(routingContext.getBodyAsString(), Availability.class);
		availability.setUserId(userId);

		Availability createdAvailability = availabilityRepository.save(availability);
		routingContext.response().putHeader("content-type", "application/json").setStatusCode(201)
				.end(Json.encodePrettily(createdAvailability));
	}

	private void handleUpdateAvailability(RoutingContext routingContext) {
		String userId = routingContext.request().getParam("userId");
		String availabilityId = routingContext.request().getParam("id");
		Availability availability = Json.decodeValue(routingContext.getBodyAsString(), Availability.class);
		availability.setId(availabilityId);
		availability.setUserId(userId);

		availabilityRepository.findById(availabilityId).ifPresentOrElse(existingAvailability -> {
			availabilityRepository.save(availability);
			routingContext.response().putHeader("content-type", "application/json")
					.end(Json.encodePrettily(availability));
		}, () -> routingContext.response().setStatusCode(404).end());
	}

	private void handleDeleteAvailability(RoutingContext routingContext) {
		String availabilityId = routingContext.request().getParam("id");
		availabilityRepository.deleteById(availabilityId);
		routingContext.response().setStatusCode(204).end();
	}

	private void handleGetOverlap(RoutingContext routingContext) {
		String userId1 = routingContext.request().getParam("userId1");
		String userId2 = routingContext.request().getParam("userId2");

		List<Availability> availabilitiesUser1 = availabilityRepository.findByUserId(userId1);
		List<Availability> availabilitiesUser2 = availabilityRepository.findByUserId(userId2);

		List<Availability> overlap = findOverlappingAvailabilities(availabilitiesUser1, availabilitiesUser2);

		routingContext.response().putHeader("content-type", "application/json").end(Json.encodePrettily(overlap));
	}

	private List<Availability> findOverlappingAvailabilities(List<Availability> availabilitiesUser1,
			List<Availability> availabilitiesUser2) {
		List<Availability> overlaps = new ArrayList<>();

		for (Availability avail1 : availabilitiesUser1) {
			for (Availability avail2 : availabilitiesUser2) {
				LocalDateTime latestStart = avail1.getStart().isAfter(avail2.getStart()) ? avail1.getStart()
						: avail2.getStart();
				LocalDateTime earliestEnd = avail1.getEnd().isBefore(avail2.getEnd()) ? avail1.getEnd()
						: avail2.getEnd();

				if (latestStart.isBefore(earliestEnd)) {
					Availability overlap = new Availability();
					overlap.setStart(latestStart);
					overlap.setEnd(earliestEnd);
					overlaps.add(overlap);
				}
			}
		}

		return overlaps;
	}
}
