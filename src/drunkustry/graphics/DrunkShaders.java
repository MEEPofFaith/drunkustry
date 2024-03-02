package drunkustry.graphics;

import arc.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.Texture.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.util.*;
import mindustry.graphics.Shaders.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class DrunkShaders{
    public static HallucinationShader colorHallucination;
    public static AberrationShader chromaticAberration;
    public static DistortionShader distortion;
    public static InversionShader inversion;

    public static void init(){
        colorHallucination = new HallucinationShader();
        chromaticAberration = new AberrationShader();
        distortion = new DistortionShader();
        inversion = new InversionShader();
    }

    public static class HallucinationShader extends DrunkScreenShader{
        public HallucinationShader(){
            super("colorHallucination");
        }

        @Override
        public void applyOther(){
            setUniformf("u_alpha", settings.getFloat("du-color-alpha"));
        }

        @Override
        public float timeScale(){
            return settings.getFloat("du-color-speed");
        }
    }

    public static class AberrationShader extends DrunkScreenShader{
        public AberrationShader(){
            super("chromaticAberration");
        }

        @Override
        public void applyOther(){
            setUniformf("u_scl", settings.getFloat("du-aberration-amount"));
        }

        @Override
        public float timeScale(){
            return settings.getFloat("du-aberration-speed");
        }
    }

    public static class DistortionShader extends DrunkScreenShader{
        public DistortionShader(){
            super("distortion");
        }

        @Override
        public void applyOther(){
            setUniformf("u_scl", settings.getFloat("du-distortion-amount"));
            setUniformf("u_insanity", settings.getFloat("du-distortion-insanity"));
        }

        @Override
        public float timeScale(){
            return settings.getFloat("du-distortion-speed");
        }
    }

    public static class InversionShader extends DrunkShader{
        public float lerp = 0f;

        public InversionShader(){
            super("inversion");
        }

        @Override
        public void apply(){
            float freq = settings.getFloat("du-inversion-freq", 1f);
            float t = Time.time / 60f * Mathf.PI / 4f * freq;
            float s = Mathf.sin(t, 1f, 1f) +
                Mathf.sin(t, 1.3f, 1f) +
                Mathf.sin(t, 1.7f, 1f) +
                Mathf.sin(t, 0.5f, 1f) +
                Mathf.sin(t, 0.8f, 1f);
            s /= 5f * freq * 0.5f;
            if(!state.isPaused()) inversion.lerp = Mathf.clamp(inversion.lerp + s * Time.delta, 0f, 1f);

            setUniformf("u_lerp", lerp);
            super.apply();
        }
    }

    /** Copy of {@link SurfaceShader} that's able to get my shader. */
    public static class DrunkScreenShader extends DrunkShader{
        protected Texture noiseTex;

        public DrunkScreenShader(String frag){
            super(frag);
            loadNoise();
        }

        public String textureName(){
            return "noise";
        }

        public void loadNoise(){
            Core.assets.load("sprites/" + textureName() + ".png", Texture.class).loaded = t -> {
                t.setFilter(TextureFilter.linear);
                t.setWrap(TextureWrap.repeat);
            };
        }

        public void applyOther(){
        }

        public float timeScale(){
            return 1f;
        }

        @Override
        public void apply(){
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time * timeScale());
            applyOther();

            if(hasUniform("u_noise")){
                if(noiseTex == null){
                    noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);
                }

                noiseTex.bind(1);
                buffer.getTexture().bind(0);
                setUniformi("u_noise", 1);
            }
        }
    }

    public static class DrunkShader extends Shader{
        protected FrameBuffer buffer = new FrameBuffer();
        protected final String frag;

        public DrunkShader(String frag){
            super(getShaderFi("screenspace.vert"), getShaderFi(frag + ".frag"));
            this.frag = frag;
        }

        public void begin(){
            Log.info(frag + " begin");
            buffer.resize(graphics.getWidth(), graphics.getHeight());
            buffer.begin(Color.clear);
        }

        public void end(){
            Log.info(frag + " end");
            buffer.end();
            buffer.blit(this);
        }

        @Override
        public void apply(){
            buffer.getTexture().bind(0);
        }
    }

    public static Fi getInternalShaderFi(String file){
        return files.internal("shaders/" + file);
    }

    public static Fi getShaderFi(String file){
        return tree.get("shaders/" + file);
    }
}
