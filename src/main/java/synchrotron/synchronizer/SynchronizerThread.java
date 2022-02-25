package synchrotron.synchronizer;

import org.jetbrains.annotations.NotNull;

public class SynchronizerThread extends Thread {

	private boolean doStop = false;

	@NotNull
	Repository firstRepo;

	@NotNull
	Repository secondRepo;

	@NotNull
	final Synchronizer synchronizer;

	final int interval;

	public SynchronizerThread(@NotNull Synchronizer synchronizer, @NotNull Repository firstRepo, @NotNull Repository secondRepo) {
		this(synchronizer, firstRepo, secondRepo, 5_000);
	}

	public SynchronizerThread(@NotNull Synchronizer synchronizer, @NotNull Repository firstRepo, @NotNull Repository secondRepo, int interval) {
		this.synchronizer = synchronizer;
		this.firstRepo = firstRepo;
		this.secondRepo = secondRepo;
		this.interval = interval;
	}

	@Override
	public void run() {
		try {
			while (this.keepRunning()) {
				// Work
				firstRepo = firstRepo.getSnapshot();
				secondRepo = secondRepo.getSnapshot();

				this.synchronizer.reconcile(firstRepo, secondRepo);

				//noinspection BusyWait
				Thread.sleep(this.interval);
			}
		} catch (InterruptedException interruptedException) {
			System.err.println(interruptedException.getMessage());
		} finally {
			this.doStop();
		}
	}

	private synchronized boolean keepRunning() {
		return !this.doStop;
	}

	public synchronized void doStop() {
		this.doStop = true;
	}
}
