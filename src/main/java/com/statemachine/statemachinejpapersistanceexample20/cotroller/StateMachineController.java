package com.statemachine.statemachinejpapersistanceexample20.cotroller;

import com.statemachine.statemachinejpapersistanceexample20.config.StateMachineLogListener;
import com.statemachine.statemachinejpapersistanceexample20.enums.Event;
import com.statemachine.statemachinejpapersistanceexample20.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.EnumSet;
import java.util.List;

@Controller
public class StateMachineController {

	public final static String MACHINE_ID_1 = "datajpapersist1";
	public final static String MACHINE_ID_2 = "datajpapersist2";
	private final static String[] MACHINES = new String[] { MACHINE_ID_1, MACHINE_ID_2 };

	private final StateMachineLogListener listener = new StateMachineLogListener();
	private StateMachine<Status, Event> currentStateMachine;

	@Autowired
	private StateMachineService<Status, Event> stateMachineService;

	@Autowired
	private StateMachinePersist<Status, Event, String> stateMachinePersist;

	@RequestMapping("/")
	public String home() {
		return "redirect:/state";
	}

	@RequestMapping("/state")
	public String feedAndGetStates(
			@RequestParam(value = "events", required = false) List<Event> events,
			@RequestParam(value = "machine", required = false, defaultValue = MACHINE_ID_1) String machine,
			Model model) throws Exception {
		StateMachine<Status, Event> stateMachine = getStateMachine(machine);
		if (events != null) {
			for (Event event : events) {
				stateMachine.sendEvent(event);
			}
		}
		StateMachineContext<Status, Event> stateMachineContext = stateMachinePersist.read(machine);
		model.addAttribute("allMachines", MACHINES);
		model.addAttribute("machine", machine);
		model.addAttribute("allEvents", getEvents());
		model.addAttribute("messages", createMessages(listener.getMessages()));
		model.addAttribute("context", stateMachineContext != null ? stateMachineContext.toString() : "");
		return "states";
	}

//tag::snippetA[]
	private synchronized StateMachine<Status, Event> getStateMachine(String machineId) throws Exception {
		listener.resetMessages();
		if (currentStateMachine == null) {
			currentStateMachine = stateMachineService.acquireStateMachine(machineId);
			currentStateMachine.addStateListener(listener);
			currentStateMachine.start();
		} else if (!ObjectUtils.nullSafeEquals(currentStateMachine.getId(), machineId)) {
			stateMachineService.releaseStateMachine(currentStateMachine.getId());
			currentStateMachine.stop();
			currentStateMachine = stateMachineService.acquireStateMachine(machineId);
			currentStateMachine.addStateListener(listener);
			currentStateMachine.start();
		}
		return currentStateMachine;
	}
//end::snippetA[]

	private Event[] getEvents() {
		return EnumSet.allOf(Event.class).toArray(new Event[0]);
	}

	private String createMessages(List<String> messages) {
		StringBuilder buf = new StringBuilder();
		for (String message : messages) {
			buf.append(message);
			buf.append("\n");
		}
		return buf.toString();
	}
}
