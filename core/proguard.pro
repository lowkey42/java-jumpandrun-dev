-injars       game.jar
-libraryjars lib/jsfml.jar
-libraryjars lib/json_simple-1.1.jar
-libraryjars lib/jbox2d-library-2.2.1.2-SNAPSHOT.jar
-outjars      game_opt.jar
-libraryjars  <java.home>/lib/rt.jar
-printmapping game_opt.map
-optimizationpasses 1
-target 1.7
-dontshrink

-keep public class de.secondsystem.game01.Main {
    public static void main(java.lang.String[]);
}
-keep class de.secondsystem.game01.impl.game.ScriptApiImpl {
	public *;
}

-keep,allowoptimization class de.secondsystem.game01.impl.game.entities.GameEntityManager {
	public *;
}
-keep,allowoptimization class de.secondsystem.game01.impl.scripting.** {
	public *;
}

-keep,allowoptimization class **Factory
 -keep,allowoptimization class de.secondsystem.game01.impl.game.entities.effects.*
 
 -keep,allowoptimization class de.secondsystem.game01.impl.game.entities.events.*
 
 -keepclassmembers,allowoptimization enum * {
     public static **[] values();
	 public static ** valueOf(java.lang.String);
 }
