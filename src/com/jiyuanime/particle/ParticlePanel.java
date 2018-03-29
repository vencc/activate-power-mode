package com.jiyuanime.particle;

import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.jiyuanime.config.Config;
import com.jiyuanime.shape.RoughRoundShape;
import com.jiyuanime.shape.RoundShape;
import com.jiyuanime.shape.StarShape;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * 粒子容器
 * <p>
 * Created by KADO on 15/12/15.
 */
public class ParticlePanel implements Runnable, Border {
    private static final int MAX_PARTICLE_COUNT = 100;

    private static ParticlePanel mParticlePanel;

    private int mParticleIndex = 0;
    private ConcurrentHashMap<String, ParticleView> mParticleViews = new ConcurrentHashMap<>();

    private JComponent mNowEditorJComponent;

    private int mParticleAreaWidth, mParticleAreaHeight;
    private BufferedImage mParticleAreaImage;
    private Graphics2D mParticleAreaGraphics;
    private Point mCaretPoint = new Point();
    private Point mParticleAreaSpeed = new Point();

    private Point mCurrentCaretPosition = null;

    private Thread mPThread;

    private boolean isEnable = false;

    private Config.State state = Config.getInstance().state;

    public static ParticlePanel getInstance() {
        if (mParticlePanel == null) {
            mParticlePanel = new ParticlePanel();
        }
        return mParticlePanel;
    }

    private ParticlePanel() {
    }

    @Override
    public void run() {
        while (isEnable) {
            if (mParticleAreaGraphics != null) {
                mParticleAreaGraphics.setBackground(new Color(0x00FFFFFF, true));
                mParticleAreaGraphics.clearRect(0, 0, mParticleAreaWidth * 2, mParticleAreaHeight * 2);

                for (String key : mParticleViews.keySet()) {
                    ParticleView particleView = mParticleViews.get(key);
                    if (particleView != null && particleView.isEnable()) {
                        /*mParticleAreaGraphics.setColor(particleView.mColor);
                        mParticleAreaGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        mParticleAreaGraphics.fillOval((int) particleView.x, (int) particleView.y, state.PARTICLE_MAX_SIZE, state.PARTICLE_MAX_SIZE);*/
                        // modify by vencc on 2018/3/30
                        try {
                            Class cls = Class.forName("com.jiyuanime.shape."+state.PARTICLE_SHAPE);
                            Method method=cls.getMethod("getGraphics",Graphics2D.class,int.class,int.class,int.class,Color.class);
                            method.invoke(null,mParticleAreaGraphics,(int) particleView.x, (int) particleView.y, state.PARTICLE_MAX_SIZE,particleView.mColor);
                            update(particleView);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                if (mNowEditorJComponent != null)
                    mNowEditorJComponent.repaint();
            }

            try {
                Thread.sleep(35);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (mParticleAreaImage == null)
            return;

        Graphics2D graphics2 = (Graphics2D) g;
        graphics2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        Point point = ParticlePositionCalculateUtil.getParticleAreaPositionOnEditorArea(mCaretPoint, mParticleAreaWidth, mParticleAreaHeight);
        graphics2.drawImage(mParticleAreaImage, point.x, point.y, c);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return JBUI.emptyInsets();
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    public void init(JComponent jComponent) {

        if (mParticleViews == null) {
            mParticleIndex = 0;
            mParticleViews = new ConcurrentHashMap<>();
        }

        if (mPThread == null)
            mPThread = new Thread(this);

        if (mNowEditorJComponent != null) {
            mNowEditorJComponent.setBorder(null);
            mNowEditorJComponent = null;
        }

        mNowEditorJComponent = jComponent;

        updateDrawer(jComponent);
    }

    public void reset(JComponent jComponent) {
        clear();
        init(jComponent);
        setEnableAction(true);
    }

    public void clear() {
        isEnable = false;

        if (mPThread != null) {
            mPThread.suspend();
            mPThread = null;
        }

        if (mNowEditorJComponent != null) {
            mNowEditorJComponent.setBorder(null);
            mNowEditorJComponent = null;
        }

        if (mParticleAreaGraphics != null)
            mParticleAreaGraphics = null;

        if (mParticleAreaImage != null)
            mParticleAreaImage = null;
    }

    public void destroy() {
        clear();

        if (mParticleViews != null)
            mParticleViews.clear();
        mParticleViews = null;
    }

    public void update(ParticleView particleView) {
        if (particleView.mAlpha <= 0.1) {
            particleView.setEnable(false);
            return;
        }

        particleView.update();
    }

    public void updateDrawer(Component jComponent) {
        mParticleAreaWidth = ParticlePositionCalculateUtil.getParticleAreaWidth(jComponent.getFont().getSize());
        mParticleAreaHeight = ParticlePositionCalculateUtil.getParticleAreaHeight(jComponent.getFont().getSize());

        //mParticleAreaImage = new BufferedImage(mParticleAreaWidth, mParticleAreaHeight, BufferedImage.TYPE_INT_BGR);
        // modify by vencc on 2018/3/30
        mParticleAreaImage = UIUtil.createImage(jComponent, mParticleAreaWidth, mParticleAreaHeight, BufferedImage.TYPE_INT_BGR);

        mParticleAreaGraphics = mParticleAreaImage.createGraphics();
        /** 设置 透明窗体背景 */
        mParticleAreaImage = mParticleAreaGraphics.getDeviceConfiguration().createCompatibleImage(mParticleAreaWidth, mParticleAreaHeight, Transparency.TRANSLUCENT);
        mParticleAreaGraphics.dispose();
        mParticleAreaGraphics = mParticleAreaImage.createGraphics();
        /** 设置 透明窗体背景 END */
    }

    private void particlesDeviation(Point speed) {
        for (String key : mParticleViews.keySet()) {
            ParticleView particle = mParticleViews.get(key);
            particle.setX(particle.x - speed.x);
            particle.setY(particle.y - speed.y);
        }
    }

    public void setEnableAction(boolean isEnable) {
        this.isEnable = isEnable;
        if (this.isEnable) {
            if (mParticleAreaImage != null && mParticleAreaGraphics != null && mNowEditorJComponent != null) {
                if (mPThread == null) {
                    mPThread = new Thread(this);
                }
                mPThread.start();
            } else {
                this.isEnable = false;
                System.out.println("还没初始化 ParticlePanel");
            }
        } else {
            destroy();
        }
    }

    public void sparkAtPosition(Point position, Color color, int fontSize) {
        mParticleAreaSpeed.setLocation(position.x - mCaretPoint.x, position.y - mCaretPoint.y);
        particlesDeviation(mParticleAreaSpeed);
        mCaretPoint = position;

        mParticleAreaWidth = ParticlePositionCalculateUtil.getParticleAreaWidth(fontSize);
        mParticleAreaHeight = ParticlePositionCalculateUtil.getParticleAreaHeight(fontSize);

        Point particlePoint = ParticlePositionCalculateUtil.getParticlePositionOnArea(mParticleAreaWidth, mParticleAreaHeight);

        int particleNumber = 5 + (int) Math.round(Math.random() * state.PARTICLE_MAX_COUNT);

        for (int i = 0; i < particleNumber; i++) {

            if (mParticleIndex >= MAX_PARTICLE_COUNT) {
                mParticleViews.get(String.valueOf(mParticleIndex % MAX_PARTICLE_COUNT)).reset(particlePoint, color, true);
            } else {
                ParticleView particleView = new ParticleView(particlePoint, color, true);
                mParticleViews.put(String.valueOf(mParticleIndex), particleView);
            }

            if (mParticleIndex < MAX_PARTICLE_COUNT * 10)
                mParticleIndex++;
            else
                mParticleIndex = MAX_PARTICLE_COUNT;
        }
    }

    public void sparkAtPositionAction(Color color, int fontSize) {
        if (mCurrentCaretPosition != null) {
            sparkAtPosition(mCurrentCaretPosition, color, fontSize);
            mCurrentCaretPosition = null;
        }
    }

    public boolean isEnable() {
        return isEnable;
    }

    public JComponent getNowEditorJComponent() {
        return mNowEditorJComponent;
    }

    public void setCurrentCaretPosition(Point currentCaretPosition) {
        mCurrentCaretPosition = currentCaretPosition;
    }
}
