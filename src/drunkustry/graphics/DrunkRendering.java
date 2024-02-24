package drunkustry.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;

import static arc.Core.*;
import static drunkustry.graphics.DrunkShaders.*;

public class DrunkRendering{
    public static final float begin = Layer.min;
    public static final float end = Layer.max;

    public static void init(){
        initAberration();
        initColor();
        initDistortion();
        initInversion();
    }

    private static void initAberration(){
        if(!settings.getBool("du-aberration", true)) return;

        Events.run(Trigger.drawOver, () -> {
            Draw.draw(begin + 0.02f, () -> {
                chromaticAberration.begin();
            });
            Draw.draw(end, () -> {
                chromaticAberration.end();
            });
        });
    }

    private static void initColor(){
        if(!settings.getBool("du-color", true)) return;

        Events.run(Trigger.drawOver, () -> {
            Draw.draw(end, () -> {
                colorHallucination.begin();
                Draw.rect();
                colorHallucination.end();
            });
        });
    }

    private static void initDistortion(){
        if(!settings.getBool("du-distortion", true)) return;

        Events.run(Trigger.drawOver, () -> {
            Draw.draw(begin + 0.01f, () -> {
                distortion.begin();
            });
            Draw.draw(end, () -> {
                distortion.end();
            });
        });
    }

    private static void initInversion(){
        if(!settings.getBool("du-inversion", true)) return;

        Events.run(Trigger.drawOver, () -> {
            Draw.draw(begin, () -> {
                inversion.begin();
            });
            Draw.draw(end, () -> {
                float t = Time.time / 60f * Mathf.PI / 4f * settings.getFloat("du-inversion", 1f);
                inversion.lerp = (Mathf.absin(t, 1f, 1f) + //TODO better lerp. Gets stuck in middle.
                    Mathf.absin(t, 1.3f, 1f) +
                    Mathf.absin(t, 1.7f, 1f) +
                    Mathf.absin(t, 0.5f, 1f) +
                    Mathf.absin(t, 0.8f, 1f)) / 5f;
                inversion.end();
            });
        });
    }
}
