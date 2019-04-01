package com.statemachine.statemachinejpapersistanceexample20.config;

import static com.statemachine.statemachinejpapersistanceexample20.enums.PartyStatus.*;

import java.util.Arrays;
import java.util.HashSet;

import com.statemachine.statemachinejpapersistanceexample20.enums.PartyEvent;
import com.statemachine.statemachinejpapersistanceexample20.enums.PartyStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
              .state(PartyStatus.COMPLETE)
              .fork(PartyStatus.FORK_UNDER_REVIEW)
              .state(PartyStatus.UNDER_REVIEW)
//              .state(COMPLIANCE_STATUS)
              .join(PartyStatus.JOIN_UNDER_REVIEW)
              .state(PartyStatus.ACTIVE)
              .state(LIVE)
              //						.state(PartyStatus.DELETED)
              //						.state(PartyStatus.CLOSED)
              //						.state(PartyStatus.SUSPENDED)

              .and()
              .withStates()
              .parent(PartyStatus.UNDER_REVIEW)
              .initial(PartyStatus.KYC_NOT_DONE)
              //						.states(new HashSet<>(Arrays.asList(PartyStatus.KYC_IN_PROGRESS, PartyStatus.KYC_UNDER_REVIEW, PartyStatus.KYC_WAITING_FOR_DOCUMENTS)))
              .end(PartyStatus.KYC_NOT_REQUIRED)
              .end(PartyStatus.KYC_COMPLETED)

              .and()
              .withStates()
              .parent(PartyStatus.UNDER_REVIEW)
              .initial(PartyStatus.SANCTION_NOT_DONE)
              .states(new HashSet<>(Arrays.asList(PartyStatus.SANCTION_IN_PROGRESS
                                                  //										,
                                                  //															PartyStatus.SANCTION_UNCONFIRMED,
                                                  //															PartyStatus.SANCTION_CONFIRMED,
                                                  //															PartyStatus.SANCTION_WAIVED
              )))
              .end(PartyStatus.SANCTION_PASSED)

              .and()
              .withStates()
              .parent(PartyStatus.UNDER_REVIEW)
              .initial(PartyStatus.PEP_NOT_DONE)
              .states(new HashSet<>(Arrays.asList(PartyStatus.PEP_IN_PROGRESS
                                                  //										,
                                                  //															PartyStatus.PEP_UNCONFIRMED,
                                                  //															PartyStatus.PEP_CONFIRMED,
                                                  //															PartyStatus.PEP_WAIVED
              )))
              .end(PartyStatus.PEP_PASSED)

//              .and()
//              .withStates()
//              .parent(COMPLIANCE_STATUS)
//              .initial(PartyStatus.COMPLIANCE_NOT_DONE)
//              .state(PartyStatus.COMPLIANCE_UNDER_REVIEW)
//              .state(PartyStatus.COMPLIANCE_PASSED)
//              .end(PartyStatus.COMPLIANCE_DONE);
        ;
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PartyStatus, PartyEvent> transitions) throws Exception {
        transitions.
                                   withExternal()
                   .source(INCOMPLETE)
                   .target(COMPLETE)
                   .event(PartyEvent.NEW)
                   //						.guard(isPartyAndAddressComplete())
                   .and()

                   //						.withExternal()
                   //						.source(PartyStatus.INCOMPLETE)
                   //						.target(PartyStatus.COMPLETE)
                   //						.event(PartyEvent.UPDATE)
                   ////						.guard(isPartyAndAddressComplete())
                   //						.and()

                   .withExternal()
                   .source(COMPLETE)
                   .target(FORK_UNDER_REVIEW)
                   //						.guard(isPartyAndAddressComplete())
                   .and()

                   .withFork() // FORK
                   .source(FORK_UNDER_REVIEW)
                   .target(UNDER_REVIEW)
//                   .target(COMPLIANCE_STATUS)
                   .and()

                   // KYC

                   .withExternal()
                   .source(KYC_NOT_DONE)
                   .target(KYC_NOT_REQUIRED)
                   .event(PartyEvent.KYC_NOT_REQUIRED)
                   //						.guard(isPartyEqualTo(PartyType.BENEFICIARY))
                   .action((ctx) -> System.out.println("kyc-not-required"))
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

                   // PEP
                   .withExternal()
                   .source(PEP_NOT_DONE)
                   .target(PEP_IN_PROGRESS)
                   .and()

                   .withExternal()
                   .source(PEP_IN_PROGRESS)
                   .target(PEP_PASSED)
                   .event(PartyEvent.PEP_PASSED)
                   .action((ctx) -> System.out.println("pep-passed"))
                   .and()

//                   .withExternal()
//                   .source(COMPLIANCE_NOT_DONE)
//                   .target(COMPLIANCE_UNDER_REVIEW)
//                   .event(PartyEvent.COMPLIANCE_UNDER_REVIEW)
//                   .and()
//
//                   .withExternal()
//                   .source(COMPLIANCE_UNDER_REVIEW)
//                   .target(COMPLIANCE_PASSED)
//                   .event(PartyEvent.COMPLIANCE_PASSED)
//                   .and()
//
//                   .withExternal()
//                   .source(COMPLIANCE_PASSED)
//                   .target(COMPLIANCE_DONE)
//                   .and()

                   // JOIN
                   .withJoin()
//                   .source(COMPLIANCE_STATUS)
                   .source(UNDER_REVIEW)
                   .target(JOIN_UNDER_REVIEW)
                   .and()

                   .withExternal()
                   .source(JOIN_UNDER_REVIEW)
                   .target(ACTIVE)
                   .guard(isCompliancePassed())
                   .action(ctx -> System.out.println("passed or not"))
                    .and()

                   .withExternal()
                   .source(ACTIVE)
                   .target(PartyStatus.LIVE)
                   .guard(isCompliancePassed())
                   .action(ctx -> System.out.println("passed"))
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
