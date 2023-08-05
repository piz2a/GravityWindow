import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GravityWindow extends JFrame implements Runnable {

    int delay = 25;
    int g = 100;

    Thread th;
    Point grabMousePoint;
    int width, height;
    double deltaX, deltaY;
    boolean grabbed = false;
    boolean pinned = false;
    double x, y;

    public GravityWindow(int width, int height, int locX, int locY) {
        setTitle(getClass().getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(width, height);
        setLocation(locX, locY);
        //setUndecorated(true);
        //setOpacity(0.5f);
        setVisible(true);

        this.width = width;
        this.height = height;
        this.x = locX;
        this.y = locY;

        getContentPane().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)) {
                    grabbed = true;
                    grabMousePoint = e.getPoint();
                    //System.out.println("Mouse pressed");
                } else if(SwingUtilities.isRightMouseButton(e)) {
                    pinned = !pinned;
                }
            }

            public void mouseReleased(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)) {
                    grabbed = false;
                }
            }
        });

        th = new Thread(this);
        th.start();
    }

    public void run() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();

        while(true) {
            try {
                Thread.sleep(delay);
            } catch(InterruptedException e) {
                return;
            }
            //System.out.println("x: " + x + " y: " + y);
            if(grabbed) {
                Point mousePoint = getMousePosition(); //MouseInfo.getPointerInfo().getLocation();
                if(mousePoint == null) {
                    grabbed = false;
                } else {
                    deltaX = mousePoint.x - grabMousePoint.x - 9;
                    deltaY = mousePoint.y - grabMousePoint.y - 38;
                    //System.out.println("x: " + deltaX + " y: " + deltaY);
                }
            } else if (pinned) {
                deltaX = deltaY = 0;
            } else {
                // 속도 증가
                deltaY += (double)(g * delay) / 1000;

                // 가로 벽에 닿으면
                if(x < 0 || screenWidth - width < x) {
                    if(x < 0) {  // 왼쪽
                        x = 0;
                    } else if(screenWidth - width < x) {  // 오른쪽
                        x = screenWidth - width;
                    }
                    deltaX = -deltaX * .3;
                    deltaY = deltaY * .9;
                }
                // 세로 벽에 닿으면
                if(y < 0 || screenHeight - height < y) {
                    if(y < 0) {  // 위쪽
                        y = 0;
                        deltaY = -deltaY * .9;
                    } else if(screenHeight - height < y) {  // 아래쪽
                        y = screenHeight - height;
                        deltaY = -deltaY * .3;
                    }
                    deltaX = deltaX * .5;
                }
            }
            // 이동
            x += deltaX;
            y += deltaY;
            setLocation((int)x, (int)y);
        }
    }

    public static void main(String[] args) {
        new GravityWindow(300, 300, 300, 300);
    }
}
