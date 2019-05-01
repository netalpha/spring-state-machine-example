package com.statemachine.statemachinejpapersistanceexample20.config;

import static com.statemachine.statemachinejpapersistanceexample20.enums.PartyStatus.*;

import com.statemachine.statemachinejpapersistanceexample20.enums.PartyEvent;
import com.statemachine.statemachinejpapersistanceexample20.enums.PartyStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<PartyStatus, PartyEvent> {

    @Autowired
    private JpaStateMachineRepository jpaStateMachineRepository;

    @Override
    public void configure(StateMachineConfigurationConfigurer<PartyStatus, PartyEvent> config) throws Exception {
        config.withPersistence().runtimePersister(stateMachineRuntimePersister());
    }

    @Override
    public void configure(StateMachineStateConfigurer<PartyStatus, PartyEvent> states) throws Exception {
        states.withStates()
              .initial(S0)
              .state(PartyStatus.S0)
              .and()

              .withStates()
              .parent(S0)
              .initial(PartyStatus.INCOMPLETE)
              .state(INCOMPLETE)
              .choice(PartyStatus.CHOICE_PROFILE_COMPLETE)
              .state(PartyStatus.COMPLETE)
              .fork(PartyStatus.FORK_UNDER_REVIEW)
              .state(PartyStatus.UNDER_REVIEW)
              .join(PartyStatus.JOIN_UNDER_REVIEW)
              .state(COMPLIANCE_STATUS)
              .state(ACTIVE)
              .state(PartyStatus.DELETED)

              .and()
              .withStates()
              .parent(PartyStatus.UNDER_REVIEW)
              .initial(PartyStatus.KYC_NOT_DONE)
              .state(KYC_IN_PROGRESS)
              .state(PartyStatus.KYC_NOT_REQUIRED)
              .end(PartyStatus.KYC_DONE)

              .and()
              .withStates()
              .parent(PartyStatus.UNDER_REVIEW)
              .initial(PartyStatus.SANCTION_NOT_DONE)
              .state(PartyStatus.SANCTION_IN_PROGRESS)
              .state(PartyStatus.SANCTION_PASSED)
              .end(SANCTION_DONE)

              .and()
              .withStates()
              .parent(PartyStatus.UNDER_REVIEW)
              .initial(PartyStatus.PEP_NOT_DONE)
              .state(PartyStatus.PEP_IN_PROGRESS)
              .state(PartyStatus.PEP_PASSED)
              .end(PEP_DONE)

              .and()
              .withStates()
                .parent(UNDER_REVIEW)
                .initial(PartyStatus.BANK_NOT_DONE)
                 .choice(PartyStatus.CHOICE_BANK_CHECK)
                .state(PartyStatus.BANK_IN_PROGRESS)
                .state(PartyStatus.BANK_PASSED)
                .state(PartyStatus.BANK_NOT_REQUIRED)
                .end(PartyStatus.BANK_DONE)

        ;
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PartyStatus, PartyEvent> transitions) throws Exception {
        transitions.withExternal()
                   .source(INCOMPLETE)
                   .target(CHOICE_PROFILE_COMPLETE)
                   .event(PartyEvent.NEW)
                   .and()

                   .withChoice()
                   .source(CHOICE_PROFILE_COMPLETE)
                   .first(COMPLETE, ctx -> true)
                   .last(INCOMPLETE)
                   .and()

                   .withExternal()
                   .source(COMPLETE)
                   .target(FORK_UNDER_REVIEW)
                   .and()

                   .withFork() // FORK
                   .source(FORK_UNDER_REVIEW)
                   .target(UNDER_REVIEW)
                   .and()

                   // KYC

                   .withExternal()
                   .source(KYC_NOT_DONE)
                   .target(KYC_NOT_REQUIRED)
                   .event(PartyEvent.KYC_NOT_REQUIRED)
                   .action((ctx) -> System.out.println("kyc-not-required"))
                   .and()

                   .withExternal()
                   .source(KYC_NOT_REQUIRED)
                   .target(KYC_DONE)
                   .action(ctx -> System.out.println("kyc-done"))
                   .and()

                   // Sanction
                   .withExternal()
                   .source(SANCTION_NOT_DONE)
                   .target(SANCTION_IN_PROGRESS)
                   .event(PartyEvent.SANCTION_INPROGRESS)
                   .action(ctx -> System.out.println("sanc-in-progress"))
                   .and()

                   .withExternal()
                   .source(SANCTION_IN_PROGRESS)
                   .target(SANCTION_PASSED)
                   .event(PartyEvent.SANCTION_PASSED)
                   .action((ctx) -> System.out.println("sanc-passed"))
                   .and()

                   .withExternal()
                   .source(SANCTION_PASSED)
                   .target(SANCTION_DONE)
                   .action(ctx -> System.out.println("sanc-done"))
                   .and()

                   // PEP
                   .withExternal()
                   .source(PEP_NOT_DONE)
                   .target(PEP_IN_PROGRESS)
//                   .event(PartyEvent.PEP_INPROGRESS)
                   .action(ctx -> System.out.println("pep-inprogress"))
                   .and()

                   .withExternal()
                   .source(PEP_IN_PROGRESS)
                   .target(PEP_PASSED)
                   .event(PartyEvent.PEP_PASSED)
                   .action((ctx) -> System.out.println("pep-passed"))
                   .and()

                   .withExternal()
                   .source(PEP_PASSED)
                   .target(PEP_DONE)
                   .action(ctx -> System.out.println("pep-done"))
                   .and()

                   // bank
                   .withExternal()
                   .source(BANK_NOT_DONE)
                   .target(CHOICE_BANK_CHECK)
                   .action(ctx -> System.out.println("bank-choice"))
                   .and()

                   .withChoice()
                   .source(CHOICE_BANK_CHECK)
                   .first(BANK_IN_PROGRESS, this::isBankCheckNeeded)
                   .last(BANK_NOT_REQUIRED, ctx -> System.out.println("bank-not-required"))
                   .and()

                   .withExternal()
                   .source(BANK_NOT_REQUIRED)
                   .target(BANK_DONE)
                   .and()

                   .withExternal()
                   .source(BANK_IN_PROGRESS)
                   .target(BANK_PASSED)
                   .event(PartyEvent.BANK_PASSED)
                   .action((ctx) -> System.out.println("bank-passed"))
                   .and()

                   .withExternal()
                   .source(BANK_PASSED)
                   .target(BANK_DONE)
                   .action(ctx -> System.out.println("bank-done"))
                   .and()

                   // JOIN
                   .withJoin()
                   .source(UNDER_REVIEW)
                   .target(JOIN_UNDER_REVIEW)
                   .and()

                   .withExternal()
                   .source(JOIN_UNDER_REVIEW)
                   .target(COMPLIANCE_STATUS)
                   .guard(ctx -> true)
                   .action(ctx -> System.out.println("compliance-status"))
                   .and()

                   .withExternal()
                   .source(COMPLIANCE_STATUS)
                   .target(ACTIVE)
                   .guard(isCompliancePassed())
                   .action(ctx -> System.out.println("compliance passed"))
                   .and()

                   .withInternal()
                   .source(S0)
                   .event(PartyEvent.COMPLIANCE_UNDER_REVIEW)
                   .action(ctx -> ctx.getExtendedState().getVariables().put("compliance_status", "review"))
                   .and()

                   .withInternal()
                   .source(S0)
                   .event(PartyEvent.COMPLIANCE_PASSED)
                   .action(ctx -> ctx.getExtendedState().getVariables().put("compliance_status", "passed"))

        ;
    }

    private boolean isBankCheckNeeded(StateContext<PartyStatus, PartyEvent> context) {
        return false;
    }

    private Guard<PartyStatus, PartyEvent> isCompliancePassed() {

        return context -> {
            boolean guard = String.valueOf(context.getExtendedState().get("compliance_status", String.class)).equalsIgnoreCase("passed");
            return guard;
        };
    }

    @Bean
    public StateMachineRuntimePersister<PartyStatus, PartyEvent, String> stateMachineRuntimePersister() {
        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }

    @Bean
    public StateMachineService<PartyStatus, PartyEvent> stateMachineService(StateMachineFactory<PartyStatus, PartyEvent> stateMachineFactory,
                                                                            StateMachineRuntimePersister<PartyStatus, PartyEvent, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService(stateMachineFactory, stateMachineRuntimePersister);
    }
}
