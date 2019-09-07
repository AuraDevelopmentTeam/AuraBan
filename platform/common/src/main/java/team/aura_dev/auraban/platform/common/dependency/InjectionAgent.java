package team.aura_dev.auraban.platform.common.dependency;

import java.lang.instrument.Instrumentation;

public class InjectionAgent {
  public static void agentmain(String agentArgs, Instrumentation inst) {
    System.out.println(agentArgs);
    System.out.println("Hi from the agent!");
    System.out.println("I've got instrumentation!: " + inst);
  }
}
