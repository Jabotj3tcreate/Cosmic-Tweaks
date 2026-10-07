package com.cosmictweaks.screen;

import com.cosmictweaks.CosmicTweaksClient;
import com.cosmictweaks.hud.HudLayout;
import com.cosmictweaks.hud.HudLayout.ModulePosition;
import com.cosmictweaks.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.List;

public final class HudEditorScreen extends Screen {
    private final Screen parent; private String dragging,selected; private double ox,oy; private boolean snap=true;
    private static final int SIDE=220,TOP=58,BOTTOM=44,GRID=8;
    private record Entry(String id,String label,String preview,int w,int h,int dx,int dy){}
    public HudEditorScreen(Screen parent){super(Text.literal("HUD Editor"));this.parent=parent;}

    @Override public void render(DrawContext c,int mx,int my,float d){
        c.fill(0,0,width,height,0xFF090C12);
        int canvasR=width-SIDE;
        for(int x=0;x<canvasR;x+=GRID)c.fill(x,TOP,x+1,height-BOTTOM,0x142B3442);
        for(int y=TOP;y<height-BOTTOM;y+=GRID)c.fill(0,y,canvasR,y+1,0x142B3442);
        c.fill(0,0,width,TOP,0xFF0D121A); c.fill(0,TOP-1,width,TOP,0xFF364252);
        c.drawTextWithShadow(textRenderer,Text.literal("COSMIC TWEAKS"),18,13,0xFFFFFFFF);
        c.drawTextWithShadow(textRenderer,Text.literal("HUD EDITOR"),18,30,0xFF8794A6);
        c.drawTextWithShadow(textRenderer,Text.literal(snap?"SNAP  8PX":"FREE MOVE"),canvasR-105,22,0xFF9AA7B8);

        for(Entry e:entries()) drawModule(c,e,mx,my);
        drawSidebar(c,mx,my,canvasR);
        c.fill(0,height-BOTTOM,width,height,0xFF0D121A);
        c.drawTextWithShadow(textRenderer,Text.literal("DRAG  MOVE"),16,height-28,0xFF8A96A7);
        c.drawTextWithShadow(textRenderer,Text.literal("RMB  HIDE"),110,height-28,0xFF8A96A7);
        c.drawTextWithShadow(textRenderer,Text.literal("WHEEL  SCALE"),190,height-28,0xFF8A96A7);
        c.drawTextWithShadow(textRenderer,Text.literal("G  SNAP"),300,height-28,0xFF8A96A7);
        c.drawTextWithShadow(textRenderer,Text.literal("ESC  SAVE"),width-84,height-28,0xFFFFFFFF);
    }

    private void drawSidebar(DrawContext c,int mx,int my,int x){
        c.fill(x,TOP,width,height-BOTTOM,0xFF101620); c.fill(x,x==0?TOP:x, x+2,height-BOTTOM,0xFF313C4B);
        c.drawTextWithShadow(textRenderer,Text.literal("MODULES"),x+18,TOP+18,0xFFFFFFFF);
        c.drawTextWithShadow(textRenderer,Text.literal("Select, drag, and scale"),x+18,TOP+34,0xFF707D90);
        int y=TOP+54;
        for(Entry e:entries()){
            boolean a=e.id().equals(selected), en=isEnabled(e.id());
            c.fill(x+12,y,x+SIDE-12,y+34,a?0xFF293747:0xFF171E28);
            c.drawTextWithShadow(textRenderer,Text.literal(e.label()),x+22,y+7,en?0xFFFFFFFF:0xFF677385);
            c.drawTextWithShadow(textRenderer,Text.literal(en?"ON":"OFF"),x+SIDE-47,y+7,en?0xFFFFFFFF:0xFF596677);
            y+=40;
        }
        c.drawTextWithShadow(textRenderer,Text.literal("INSPECTOR"),x+18,y+12,0xFF8794A6);
        if(selected!=null){
            Entry e=find(selected); ModulePosition p=HudLayout.get(e.id(),e.dx(),e.dy());
            c.drawTextWithShadow(textRenderer,Text.literal(e.label()),x+18,y+31,0xFFFFFFFF);
            c.drawTextWithShadow(textRenderer,Text.literal(String.format("X %.0f   Y %.0f",p.x,p.y)),x+18,y+48,0xFF9CA8B8);
            c.drawTextWithShadow(textRenderer,Text.literal(String.format("SCALE %.1fx",p.scale)),x+18,y+65,0xFF9CA8B8);
            c.drawTextWithShadow(textRenderer,Text.literal(p.visible?"VISIBLE":"HIDDEN"),x+18,y+82,p.visible?0xFFFFFFFF:0xFF687587);
            c.drawTextWithShadow(textRenderer,Text.literal("RMB toggles visibility"),x+18,y+105,0xFF687587);
        } else c.drawTextWithShadow(textRenderer,Text.literal("Click a module"),x+18,y+34,0xFF687587);
    }

    private void drawModule(DrawContext c,Entry e,int mx,int my){
        ModulePosition p=HudLayout.get(e.id(),e.dx(),e.dy()); double s=Math.max(.5,Math.min(2,p.scale));
        int x=(int)Math.round(p.x),y=(int)Math.round(p.y),w=(int)Math.round(e.w*s),h=(int)Math.round(e.h*s);
        boolean hover=inside(mx,my,x,y,w,h),active=e.id().equals(selected),enabled=isEnabled(e.id());
        c.fill(x-2,y-2,x+w+2,y+h+2,active?0xFFFFFFFF:0x00000000);
        c.fill(x,y,x+w,y+h,!enabled?0xFF141A23:(active?0xFF263544:(hover?0xFF202B38:0xE8141B24)));
        c.fill(x,y,x+w,y+2,active?0xFFFFFFFF:0xFF526073);
        c.drawTextWithShadow(textRenderer,Text.literal(enabled?e.preview():e.label()+"  •  DISABLED"),x+9,y+7,enabled?0xFFFFFFFF:0xFF697689);
        if(active){c.drawTextWithShadow(textRenderer,Text.literal("DRAG"),x+9,y+h-12,0xFF8E9BAD);}
    }

    private List<Entry> entries(){return List.of(
        new Entry("FPS","FPS","144 FPS",84,24,18,72),new Entry("PING","PING","42 ms",86,24,18,112),
        new Entry("COORDINATES","COORDINATES","120 64 -32",150,24,18,152),new Entry("KEYSTROKES","KEYSTROKES","W  A  S  D",124,48,18,192),
        new Entry("CPS","CPS","LMB 0   RMB 0",150,32,18,248),new Entry("EQUIPMENT","EQUIPMENT","ARMOR / DURABILITY",176,74,18,300),
        new Entry("FACING","FACING","NORTH",104,24,18,382),new Entry("CLOCK","CLOCK","12:00",110,24,18,422));}

    private boolean isEnabled(String id){return switch(id){case"FPS"->ModuleManager.fps();case"PING"->ModuleManager.ping();case"COORDINATES"->ModuleManager.coordinates();case"KEYSTROKES"->ModuleManager.keystrokes();case"CPS"->ModuleManager.cps();case"EQUIPMENT"->ModuleManager.armor()||ModuleManager.durability();case"FACING"->CosmicTweaksClient.CONFIG.showCompass;case"CLOCK"->CosmicTweaksClient.CONFIG.showClock;default->true;};}
    private Entry find(String id){return entries().stream().filter(e->e.id().equals(id)).findFirst().orElse(entries().get(0));}
    private Entry hit(double mx,double my){List<Entry> es=entries();for(int i=es.size()-1;i>=0;i--){Entry e=es.get(i);ModulePosition p=HudLayout.get(e.id(),e.dx(),e.dy());double s=Math.max(.5,Math.min(2,p.scale));if(inside(mx,my,p.x,p.y,e.w*s,e.h*s))return e;}return null;}

    @Override public boolean mouseClicked(net.minecraft.client.gui.Click q,boolean doubled){
        Entry e=hit(q.x(),q.y()); if(e==null)return super.mouseClicked(q,doubled); selected=e.id(); ModulePosition p=HudLayout.get(e.id(),e.dx(),e.dy());
        if(q.button()==0){dragging=e.id();ox=q.x()-p.x;oy=q.y()-p.y;return true;}
        if(q.button()==1){p.visible=!p.visible;HudLayout.save();return true;}return super.mouseClicked(q,doubled);
    }
    @Override public boolean mouseDragged(net.minecraft.client.gui.Click q,double dx,double dy){
        if(dragging==null)return super.mouseDragged(q,dx,dy);Entry e=find(dragging);ModulePosition p=HudLayout.get(e.id(),e.dx(),e.dy());double s=Math.max(.5,Math.min(2,p.scale));
        double x=q.x()-ox,y=q.y()-oy;if(snap){x=Math.round(x/GRID)*GRID;y=Math.round(y/GRID)*GRID;}
        p.x=Math.max(4,Math.min(width-SIDE-e.w*s-4,x));p.y=Math.max(TOP+4,Math.min(height-BOTTOM-e.h*s-4,y));return true;
    }
    @Override public boolean mouseReleased(net.minecraft.client.gui.Click q){if(q.button()==0&&dragging!=null){dragging=null;HudLayout.save();return true;}return super.mouseReleased(q);}
    @Override public boolean mouseScrolled(double mx,double my,double h,double v){Entry e=hit(mx,my);if(e!=null){selected=e.id();ModulePosition p=HudLayout.get(e.id(),e.dx(),e.dy());p.scale=Math.max(.5,Math.min(2,p.scale+(v>0?.1:-.1)));HudLayout.save();return true;}return super.mouseScrolled(mx,my,h,v);}
    @Override public boolean keyPressed(net.minecraft.client.input.KeyInput q){if(q.key()==256){HudLayout.save();if(client!=null)client.setScreen(parent);return true;}if(q.key()==71){snap=!snap;return true;}return super.keyPressed(q);}
    private static boolean inside(double mx,double my,double x,double y,double w,double h){return mx>=x&&mx<=x+w&&my>=y&&my<=y+h;}
}