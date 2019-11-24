
import java.lang.Runnable;
import java.lang.Thread;

public class Test2Thread implements Runnable
{
	Thread thread = null;
	Object parentThread = null;

	public Test2Thread(Object parentThread){
		this.parentThread = parentThread;
	}

	public void start(){
		thread = new Thread(this);
		thread.start();
	}

	public void run(){
		for(int i = 0; i < 60; i ++){
			System.out.println("d");
		}
		synchronized(parentThread){
			parentThread.notify();
		}
	}

}
