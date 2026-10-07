package com.cosmictweaks.screen;

import com.cosmictweaks.CosmicTweaksClient;
import com.cosmictweaks.config.CosmicConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.List;

public final class CosmicSettingsScreen extends Screen {
    private final Screen parent;
    private int category;
    private int scroll;

    private static final int W=760, H=500, SIDE=190;
    private record Toggle(String label,String desc,boolean value,Runnable action){}

    public CosmicSettingsScreen(Screen parent){super(Text.literal("Cosmic Tweaks"));this.parent=parent;}

    @Override protected void init(){}

    @Override public void render(DrawContext c,int mx,int my,float delta){
        c.fill(0,0,width,height,0xF9080B10);
        int l=(width-W)/2,t=Math.max(18,(height-H)/2),r=l+W,b=t+H;
        c.fill(l,t,r,b,0xFF111722);
        c.fill(l,t,l+3,b,0xFFCBD5E1);
        c.fill(l,t,r,t+64,0xFF0D121B);
        c.fill(l+SIDE,t+64,l+SIDE+1,b,0xFF293342);

        c.drawTextWithShadow(textRenderer,Text.literal("COSMIC TWEAKS"),l+22,t+17,0xFFFFFFFF);
        c.drawTextWithShadow(textRenderer,Text.literal("CLIENT CONTROL CENTER"),l+22,t+35,0xFF8794A6);
        c.drawTextWithShadow(textRenderer,Text.literal("1.0.0"),r-48,t+27,0xFF657284);

        String[] cats={"HUD","CLIENT","PERFORMANCE"};
        for(int i=0;i<cats.length;i++){
            int y=t+88+i*52; boolean a=category==i;
            if(a){c.fill(l+12,y-9,l+SIDE-12,y+31,0xFF263242);c.fill(l+12,y-9,l+15,y+31,0xFFFFFFFF);}
            c.drawTextWithShadow(textRenderer,Text.literal(cats[i]),l+30,y+4,a?0xFFFFFFFF:0xFF7F8B9C);
        }
        c.drawTextWithShadow(textRenderer,Text.literal(cats[category]),l+SIDE+24,t+84,0xFFFFFFFF);
        c.drawTextWithShadow(textRenderer,Text.literal(category==0?"HUD modules and overlays":category==1?"Gameplay quality-of-life":"Rendering and performance controls"),
                l+SIDE+24,t+102,0xFF7F8B9C);

        List<Toggle> ts=toggles();
        int y=t+132+scroll;
        for(Toggle x:ts){drawToggle(c,mx,my,l+SIDE+24,y,r-24,x);y+=58;}

        c.fill(l,t+H-48,r,b,0xFF0D121B);
        c.drawTextWithShadow(textRenderer,Text.literal("O  SETTINGS"),l+18,t+H-30,0xFF667386);
        c.drawTextWithShadow(textRenderer,Text.literal("H  HUD EDITOR"),l+130,t+H-30,0xFF667386);
        c.drawTextWithShadow(textRenderer,Text.literal("ESC  BACK"),r-78,t+H-30,0xFFFFFFFF);
    }

    private void drawToggle(DrawContext c,int mx,int my,int x,int y,int right,Toggle v){
        int w=right-x, h=46; boolean hov=mx>=x&&mx<right&&my>=y&&my<y+h;
        c.fill(x,y,right,y+h,hov?0xFF202A38:0xFF181F2A);
        c.fill(x,y,right,y+1,hov?0xFF66778B:0xFF303A49);
        c.drawTextWithShadow(textRenderer,Text.literal(v.label()),x+14,y+8,0xFFFFFFFF);
        c.drawTextWithShadow(textRenderer,Text.literal(v.desc()),x+14,y+24,0xFF7F8B9C);
        int sw=44, sx=right-sw-14, sy=y+11;
        c.fill(sx,sy,sx+sw,sy+22,v.value()?0xFFFFFFFF:0xFF303A49);
        c.fill(sx+(v.value()?23:2),sy+3,sx+(v.value()?41:20),sy+19,v.value()?0xFF111722:0xFF7F8B9C);
        c.drawTextWithShadow(textRenderer,Text.literal(v.value()?"ON":"OFF"),sx-27,sy+7,v.value()?0xFFFFFFFF:0xFF697587);
    }

    private List<Toggle> toggles(){
        CosmicConfig c=CosmicTweaksClient.CONFIG;
        return switch(category){
            case 0->List.of(
                new Toggle("FPS","Show current frames per second",c.showFps,()->c.showFps=!c.showFps),
                new Toggle("PING","Show network latency",c.showPing,()->c.showPing=!c.showPing),
                new Toggle("COORDINATES","Show XYZ coordinates",c.showCoordinates,()->c.showCoordinates=!c.showCoordinates),
                new Toggle("KEYSTROKES","Show movement and mouse inputs",c.showKeystrokes,()->c.showKeystrokes=!c.showKeystrokes),
                new Toggle("CPS","Show clicks per second",c.showCps,()->c.showCps=!c.showCps),
                new Toggle("ARMOR","Show equipped armor",c.showArmor,()->c.showArmor=!c.showArmor),
                new Toggle("DURABILITY","Show item durability",c.showDurability,()->c.showDurability=!c.showDurability),
                new Toggle("COMPASS","Show facing direction",c.showCompass,()->c.showCompass=!c.showCompass),
                new Toggle("WORLD CLOCK","Show Minecraft time",c.showClock,()->c.showClock=!c.showClock));
            case 1->List.of(
                new Toggle("FULLBRIGHT","Boost gamma while enabled",c.fullbright,()->{c.fullbright=!c.fullbright;if(client!=null)client.options.getGamma().setValue(c.fullbright?10.0:1.0);}),
                new Toggle("ZOOM","Enable hold-to-zoom",c.showZoom,()->c.showZoom=!c.showZoom),
                new Toggle("SPRINT TOGGLE","Enable sprint toggle support",c.sprintToggle,()->c.sprintToggle=!c.sprintToggle),
                new Toggle("SNEAK TOGGLE","Enable sneak toggle support",c.sneakToggle,()->c.sneakToggle=!c.sneakToggle),
                new Toggle("FREELOOK","Enable freelook support",c.freelook,()->c.freelook=!c.freelook),
                new Toggle("WAYPOINTS","Enable waypoint support",c.showWaypoints,()->c.showWaypoints=!c.showWaypoints));
            default->List.of(
                new Toggle("PERFORMANCE PROFILE","Use lightweight render profile",c.performanceProfile,()->c.performanceProfile=!c.performanceProfile),
                new Toggle("REDUCE PARTICLES","Reduce particle effects",c.reduceParticles,()->c.reduceParticles=!c.reduceParticles),
                new Toggle("REDUCE ENTITY DISTANCE","Reduce entity render distance",c.reduceEntityRenderDistance,()->c.reduceEntityRenderDistance=!c.reduceEntityRenderDistance),
                new Toggle("HIDE COSMETICS","Hide supported cosmetics",c.hideCosmetics,()->c.hideCosmetics=!c.hideCosmetics));
        };
    }

    @Override public boolean mouseClicked(net.minecraft.client.gui.Click click,boolean doubled){
        if(click.button()!=0)return super.mouseClicked(click,doubled);
        int l=(width-W)/2,t=Math.max(18,(height-H)/2);
        for(int i=0;i<3;i++){int y=t+79+i*52;if(inside(click.x(),click.y(),l+12,y-9,SIDE-24,40)){category=i;scroll=0;return true;}}
        int y=t+132+scroll;
        for(Toggle v:toggles()){if(inside(click.x(),click.y(),l+SIDE+24,y,W-SIDE-48,46)){v.action().run();CosmicConfig.save();return true;}y+=58;}
        return super.mouseClicked(click,doubled);
    }

    @Override public boolean mouseScrolled(double x,double y,double h,double v){
        if(x>(width-W)/2+SIDE){scroll+=(int)(-v*58);int max=Math.max(0,toggles().size()*58-(H-188));scroll=Math.max(-max,Math.min(0,scroll));return true;}
        return super.mouseScrolled(x,y,h,v);
    }

    @Override public boolean keyPressed(net.minecraft.client.input.KeyInput input){
        if(input.key()==256){CosmicConfig.save();if(client!=null)client.setScreen(parent);return true;}
        if(input.key()==72&&client!=null){client.setScreen(new HudEditorScreen(this));return true;}
        return super.keyPressed(input);
    }
    private static boolean inside(double mx,double my,int x,int y,int w,int h){return mx>=x&&mx<x+w&&my>=y&&my<y+h;}
}