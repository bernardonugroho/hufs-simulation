package BPMN_Simulation1;

/**
 * Since notify() of an object can be called only by the thread who owns it,
 * this custom-thread class is made to exactly do that.
 * 
 * Note: In the future, when SecondProcess is modified to extend Thread
 * (currently it extends Runnable), this class can be removed.
 * 
 * @author khosiawan
 * 
 */
class MyThread extends Thread {
	final Runnable r;

	public MyThread(Runnable r) {
		super(r);
		this.r = r;
	}

	public void doNotify() {
		synchronized (r) {
			r.notify();
		}
	}
}
