package cubyz.utils.gui;

/**
 * Stores the current status of a process.
 * Used for displaying status bars.
 */

public class StatusInfo {
	/**How many processes there are for this bar.*/
	public int totalProcesses = 0;
	/**Index of the current process that is done right now.*/
	public int currentProcess = 0;
	/**Name of the current process*/
	public String processName = "";
	/**Reference to the process of the current process, for multiple bars.*/
	public StatusInfo subProcess = null;
}
