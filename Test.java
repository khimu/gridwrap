
import java.lang.Thread;
import java.lang.Runnable;

public class Test implements Runnable
{
	Thread thread = null;
	Test2Thread bbthread = null;

	public static void main(String[] args){
		new Test();
	}

	public Test(){
		start();
	}

	public void start(){
		thread = new Thread(this);
		thread.start();
	}

	public void run(){
		try{
			bbthread = new Test2Thread(this);
			bbthread.start();
			System.out.println("BeforeWait");
			synchronized(this){
				this.wait();
			}
			System.out.println("AfterWait");
		}catch(InterruptedException e){
			System.out.println(""+e);
		}
	}

}
