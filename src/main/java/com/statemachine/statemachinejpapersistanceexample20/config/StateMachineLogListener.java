package com.statemachine.statemachinejpapersistanceexample20.config;

import com.statemachine.statemachinejpapersistanceexample20.enums.Event;
import com.statemachine.statemachinejpapersistanceexample20.enums.Status;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateContext.Stage;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

import java.util.LinkedList;
import java.util.List;

public class StateMachineLogListener extends StateMachineListenerAdapter<Status, Event> {

	private final LinkedList<String> messages = new LinkedList<String>();

	public List<String> getMessages() {
		return messages;
	}

	public void resetMessages() {
		messages.clear();
	}

	@Override
	public void stateContext(StateContext<Status, Event> stateContext) {
		if (stateContext.getStage() == Stage.STATE_ENTRY) {
			messages.addFirst("Enter " + stateContext.getTarget().getId());
		} else if (stateContext.getStage() == Stage.STATE_EXIT) {
			messages.addFirst("Exit " + stateContext.getSource().getId());
		} else if (stateContext.getStage() == Stage.STATEMACHINE_START) {
			messages.addLast("Machine started" + stateContext.getStateMachine().getStates());
		} else if (stateContext.getStage() == Stage.STATEMACHINE_STOP) {
			messages.addFirst("Machine stopped" + stateContext.getStateMachine().getStates());

		}
	}
}
