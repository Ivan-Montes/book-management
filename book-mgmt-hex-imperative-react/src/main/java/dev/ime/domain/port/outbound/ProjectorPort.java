package dev.ime.domain.port.outbound;

import dev.ime.domain.model.Event;

public interface ProjectorPort {
	
	void create(Event event);
	void update(Event event);
	void deleteById(Event event);
}
