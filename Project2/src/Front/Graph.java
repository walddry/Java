package Front;


import CanvasItems.Triangle;
import CanvasItems.TriangleParalel;
import CanvasItems.TypeTriangle;
import Type.Conclusion;
import Type.Premisa;
import Type.Regla;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;


public class Graph extends Canvas implements Runnable{

    private static final long serialVersionUID = 2156860797447575242L;
    private final int WIDTH = 640, HEIGTH = WIDTH / 12 * 9;
    private Thread thread;
    private boolean running = false;
    private Main main;
    private MouseListener m;
    private int size =40;
    public ArrayList<Triangle> triangle;
    private boolean open=false;
    private Triangle move;
    private int refX=0, refY=0;
    private int cont =0;
    private boolean clickOnTriangle=false, clicked = false;
    private int clickedTriangle;
        Triangle a, b;
    public Graph(Main main){

        this.main=main;
        this.setVisible(true);
        this.setBounds(0, 0, 500, 500);
        this.addKeyListener(main);
        
        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                //System.err.println(e.getWheelRotation());
                Iterator i =triangle.iterator();
                
                    
                while(i.hasNext()){
                    if(main.key && clicked){
                        Triangle auxT=(Triangle)i.next();
                        if(auxT.hitbox().contains(e.getPoint())){                                                        
                            //System.err.println("Click triangle "+ j);
                            
                            auxT.sizef+= e.getWheelRotation();
                            break;
                        }
                    }else
                    ((Triangle)i.next()).zoom +=0.01*e.getWheelRotation();
                     }
            }
        });
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int xset=0, yset=0;
                if(!clicked){
                    clicked=true;
                 Triangle auxT=null;
                    Iterator i =triangle.iterator();
                    int j=0;
                    while(i.hasNext()){
                        auxT=(Triangle)i.next();
                        if(auxT.hitbox().contains(e.getPoint())){                                                        
                            //System.err.println("Click triangle "+ j);
                            xset=auxT.x-e.getX();
                            yset=auxT.y-e.getY();
                            auxT.clicked=true;
                            clickOnTriangle=true;
                            clickedTriangle=j;
                            break;
                        }
                    j++; }
                    move=auxT;
                }


                   if(move.clicked){

                    move.distX=(-1*e.getX()-xset);
                    move.distY=(-1*e.getY()-yset);
                }
                   else{
                       move.clicked=false;
                       clicked=false;
                   }

                } 


            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
        this.addMouseListener(m=new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {
                if(cont<3 && clickOnTriangle==false){
                    //riangle.add(new Triangle(e.getX(), e.getY(), 40));
                clickOnTriangle=true;
                //System.err.println(e.getX()+", "+ e.getY()+", "+ 40+", "+ refX+", "+refY);
                cont++;

                }
                //System.err.println("Mouse Release");
                
                move.clicked=false;
                clicked=false;
                clickOnTriangle=false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        //this.setBounds(50, 50, 500, 500);
        //this.setlo
            //this.addKeyListener(new KeyInput(handler));
            //this.setBounds(10, 400, 50,50);
            //windown=new Main();
            //windown.content.add(this);
        this.triangle= new ArrayList<Triangle>();
           

    }

    public void addC(Conclusion c){
        this.triangle.forEach(new Consumer() {
            @Override
            public void accept(Object t) {
                Triangle temp = (Triangle)t;
                
                if(temp.c!=null && c.text.equals(temp.c.text)){
                    System.err.println("Argumento Paralelo");
                }
            }
        });
        if(open){
            this.triangle.get(triangle.size()-1).add(c);
            this.open=false;
            return;
        }
        else{
            this.triangle.add(new Triangle(50,50,100, TypeTriangle.Basic));   
            //System.err.println(triangle.size());
        }
    }

    public void addP(Premisa p){
        if(open){
           this.triangle.get(triangle.size()-1).add(p);
           
        }
        else{
            open=true;
            Triangle temp=new Triangle(50,50,100, TypeTriangle.Basic);
            temp.add(p);
            this.triangle.add(temp);   
            //System.err.println(triangle.size());
        }
    }
    public void addP(Premisa p, Triangle father){
        if(open){
           Regla r = this.triangle.get(triangle.size()-1).r;
           this.triangle.remove(triangle.size()-1);
           Triangle temp=new Triangle(father);
           this.triangle.add(temp);    
           temp.add(p);
           temp.add(r);
           
        }
        else{
            open=true;
            Triangle temp=new Triangle(father);
            temp.add(p);
            this.triangle.add(temp);   
            //System.err.println(triangle.size());
        }
    }
    
    public void addR(Regla r){
        if(open){           
           this.triangle.get(triangle.size()-1).add(r);
        }
        else{
            open=true;
            Triangle temp=new Triangle(50,50,100, TypeTriangle.Basic);
            temp.add(r);
            this.triangle.add(temp);   
            //System.err.println(triangle.size());
        }
    }


    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop(){
        try {
            thread.join();
            running = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void run(){

        long lastTime = System.nanoTime();
        final double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks; // cuantos nano segundos ocupa cada frame
        double delta = 0; // tiempo transcurrido desde el ultimo frame
        int frames = 0;
        long timer = System.currentTimeMillis(); // tiempo del sistema

        while(running){

            long now = System.nanoTime();
            delta += (now - lastTime) / ns; // calculo el delta
            lastTime = now; 


            if(delta >= 1){ // Master de los frames y act per seg
                tick();
                delta--;
                if(running && this.isDisplayable())
                    render();
                frames++;
            }


            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                main.setTitle("FPS: " + frames);
                frames = 0;

            }

        }
        stop();
    }

    public void tick(){
            //handler.tick();
    }

    public void render(){

        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.WIDTH, this.HEIGTH);
        g.setColor(Color.red);
        //handler.render(g);
        //new Triangle(40, 200, 100).draw(g);
          g.setColor(Color.red);

        Iterator i =triangle.iterator();
        i.forEachRemaining(new Consumer() {
            @Override
            public void accept(Object t) {
                Triangle test=(Triangle)t;
                test.draw(g);
            }
        });
        /*while(i.hasNext()){
            g.setColor(Color.red);
            //((Triangle)i.next()).act();
            test=((Triangle)i.next());
            test.draw(g);
            test2=test.hitbox();
             g.setColor(Color.blue);
            //g.drawRect(test2.x, test2.y, test2.width, test2.height);
            g.setColor(Color.WHITE);
        }*/

        g.dispose();
        bs.show();
    }

}
