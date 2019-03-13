package simulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import javafx.scene.input.Mnemonic;

public class MainFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	
	// simulation components
	
	// hardware components
	Cpu cpu;
	Memory memory;
	Disk disk;
	ExchangeArea exchangeArea;
	FastTable fastTable;
	MMU mmu;
	
	// semaphores
	int semaphoreNum;
	Semaphore[] semaphores;
	
	// queues
	Queue<Pcb> readyQueue;
	Queue<Pcb> runningQueue;
	Queue<Pcb> waitQueue;
	
	// executor
	Executor executor;
	
	// jobs
	ArrayList<Jcb> jobs ;
	
	// speed delay
	double speedDelay;
	
	// running flag
	boolean runningFlag;

	// time proposed
	int nowTime = 0;
	
	
	// visual components
	
	// config panel
	private JLabel fileNameLabel;
	private JTextField fileNameTextField;
	private JLabel delayRatioLabel;
	private JTextField delayRatioTextField;
	private JButton startButton;
	private JPanel configPanel;
	
	// jcb panel
	private JTable jcbTable;
	private JPanel jcbPanel;
	
	// time panel
	private JLabel timeLabel;
	private JPanel timePanel;
	
	// queue panel
	private JTable readyQueueTable;
	private JPanel readyQueuePanel;
	private JTable runningQueueTable;
	private JPanel runningQueuePanel;
	private JTable waitQueueTable;
	private JPanel waitQueuePanel;
	
	// ins panel
	private JTable insTable;
	private JPanel insPanel;
	
	// semaphore panel
	private JTable semaphoreTable;
	private JPanel semaphorePanel;
	
	// page table panel
	private JTable pageTableTable;
	private JPanel pageTablePanel;
	
	// fast table panel
	private JTable fastTableTable;
	private JPanel fastTablePanel;
	
	public MainFrame() {
		
		// config panel
		fileNameLabel = new JLabel("任务文件：");
		
		fileNameTextField = new JTextField("jobs.txt");
		fileNameTextField.setPreferredSize(new Dimension(130, 18));
		
		delayRatioLabel = new JLabel("延迟倍数：");
		
		delayRatioTextField = new JTextField("10.0");
		delayRatioTextField.setPreferredSize(new Dimension(130, 18));
		
		startButton = new JButton("开始运行");
		startButton.setPreferredSize(new Dimension(210, 30));
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// check params
				File jobsFile = new File(fileNameTextField.getText());
				if(!jobsFile.exists()) {
					JOptionPane.showMessageDialog(null, "找不到任务文件" + fileNameTextField.getText(), "出错", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					speedDelay = Double.parseDouble(delayRatioTextField.getText());
					if(speedDelay <= 0) {
						JOptionPane.showMessageDialog(null, "无效的延迟倍数" + speedDelay, "出错", JOptionPane.ERROR_MESSAGE);
						return;
					}
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "延迟倍数解析出错，" + e2.getMessage(), "出错", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// initialize hardware components
				initialize();
				
				// load jobs
				jobs = JobLoader.Loadjobs(fileNameTextField.getText());
				
				// go thread
				new Thread(() -> go()).start();;
				
				// update ui thread
				new UpdateUIThread().start();
				
			}
		});
		
		configPanel = new JPanel();
		configPanel.add(fileNameLabel);
		configPanel.add(fileNameTextField);
		configPanel.add(delayRatioLabel);
		configPanel.add(delayRatioTextField);
		configPanel.add(startButton);
		configPanel.setBorder(BorderFactory.createTitledBorder("配置设置"));
		configPanel.setSize(250, 110);
		configPanel.setLocation(10, 10);
		
		// jcb panel
		Object[][] jcbTableData = {};
		Object[] jcbTableHead = {"jobId", "insNum", "inTime", "status"};
		jcbTable = new JTable(jcbTableData, jcbTableHead);
		jcbTable.setShowGrid(true);
		jcbTable.setGridColor(Color.GRAY);
		jcbTable.setPreferredScrollableViewportSize(new Dimension(210, 250));
		
		jcbPanel = new JPanel();
		jcbPanel.add(new JScrollPane(jcbTable));
		jcbPanel.setBorder(BorderFactory.createTitledBorder("JCB表"));
		jcbPanel.setSize(250, 310);
		jcbPanel.setLocation(10, 130);
		
		// time panel
		timeLabel = new JLabel("0ms");
		timeLabel.setFont(new Font(timeLabel.getFont().getFontName(), Font.BOLD, 32));
		
		timePanel = new JPanel();
		timePanel.add(timeLabel);
		timePanel.setBorder(BorderFactory.createTitledBorder("系统时钟"));
		timePanel.setSize(250, 100);
		timePanel.setLocation(10, 450);
		
		// ready queue panel
		Object[][] readyQueueTableData = {};
		Object[] readyQueueTableHead = {"pcbId", "insNum", "nowIndex", "status"};
		readyQueueTable = new JTable(readyQueueTableData, readyQueueTableHead);
		readyQueueTable.setShowGrid(true);
		readyQueueTable.setGridColor(Color.GRAY);
		readyQueueTable.setPreferredScrollableViewportSize(new Dimension(260, 200));
		
		readyQueuePanel = new JPanel();
		readyQueuePanel.add(new JScrollPane(readyQueueTable));
		readyQueuePanel.setBorder(BorderFactory.createTitledBorder("就绪队列"));
		readyQueuePanel.setSize(300, 260);
		readyQueuePanel.setLocation(270, 10);
		
		// running queue panel
		Object[][] runningQueueTableData = {};
		Object[] runningQueueTableHead = {"pcbId", "nowIndex", "timePieceLeft", "status"};
		runningQueueTable = new JTable(runningQueueTableData, runningQueueTableHead);
		runningQueueTable.setShowGrid(true);
		runningQueueTable.setGridColor(Color.GRAY);
		runningQueueTable.setPreferredScrollableViewportSize(new Dimension(260, 50));
		
		runningQueuePanel = new JPanel();
		runningQueuePanel.add(new JScrollPane(runningQueueTable));
		runningQueuePanel.setBorder(BorderFactory.createTitledBorder("运行队列"));
		runningQueuePanel.setSize(300, 110);
		runningQueuePanel.setLocation(270, 280);
		
		// wait queue panel
		Object[][] waitQueueTableData = {};
		Object[] waitQueueTableHead = {"pcbId", "nowIndex", "waitTimeLeft", "waitTimeCount", "status"};
		waitQueueTable = new JTable(waitQueueTableData, waitQueueTableHead);
		waitQueueTable.setShowGrid(true);
		waitQueueTable.setGridColor(Color.GRAY);
		waitQueueTable.setPreferredScrollableViewportSize(new Dimension(260, 90));
		
		waitQueuePanel = new JPanel();
		waitQueuePanel.add(new JScrollPane(waitQueueTable));
		waitQueuePanel.setBorder(BorderFactory.createTitledBorder("等待队列"));
		waitQueuePanel.setSize(300, 150);
		waitQueuePanel.setLocation(270, 400);
		
		// ins panel
		Object[][] insTableData = {};
		Object[] insTableHead = {"insId", "instruction", "insLeftTime"};
		insTable = new JTable(insTableData, insTableHead);
		insTable.setShowGrid(true);
		insTable.setGridColor(Color.GRAY);
		insTable.setPreferredScrollableViewportSize(new Dimension(260, 300));
		
		insPanel = new JPanel();
		insPanel.add(new JScrollPane(insTable));
		insPanel.setBorder(BorderFactory.createTitledBorder("当前指令"));
		insPanel.setSize(300, 360);
		insPanel.setLocation(580, 10);
		
		// semaphore panel
		Object[][] semaphoreTableData = {};
		Object[] semaphoreTableHead = {"resourceId", "value"};
		semaphoreTable = new JTable(semaphoreTableData, semaphoreTableHead);
		semaphoreTable.setShowGrid(true);
		semaphoreTable.setGridColor(Color.GRAY);
		semaphoreTable.setPreferredScrollableViewportSize(new Dimension(260, 110));
		
		semaphorePanel = new JPanel();
		semaphorePanel.add(new JScrollPane(semaphoreTable));
		semaphorePanel.setBorder(BorderFactory.createTitledBorder("资源信号量"));
		semaphorePanel.setSize(300, 170);
		semaphorePanel.setLocation(580, 380);
		
		// page table panel
		Object[][] pageTableTableData = {};
		Object[] pageTableTableHead = {"pageId", "blockId", "exAreaId", "dwell", "vcount", "changed"};
		pageTableTable = new JTable(pageTableTableData, pageTableTableHead);
		pageTableTable.setShowGrid(true);
		pageTableTable.setGridColor(Color.GRAY);
		pageTableTable.setPreferredScrollableViewportSize(new Dimension(260, 300));
		
		pageTablePanel = new JPanel();
		pageTablePanel.add(new JScrollPane(pageTableTable));
		pageTablePanel.setBorder(BorderFactory.createTitledBorder("页表"));
		pageTablePanel.setSize(300, 360);
		pageTablePanel.setLocation(890, 10);
		
		// fast table panel
		Object[][] fastTableTableData = {};
		Object[] fastTableTableHead = {"pageId", "blockId", "vcount"};
		fastTableTable = new JTable(fastTableTableData, fastTableTableHead);
		fastTableTable.setShowGrid(true);
		fastTableTable.setGridColor(Color.GRAY);
		fastTableTable.setPreferredScrollableViewportSize(new Dimension(260, 110));
		
		fastTablePanel = new JPanel();
		fastTablePanel.add(new JScrollPane(fastTableTable));
		fastTablePanel.setBorder(BorderFactory.createTitledBorder("快表"));
		fastTablePanel.setSize(300, 170);
		fastTablePanel.setLocation(890, 380);
		
        this.setLayout(null);
        this.add(configPanel);
        this.add(jcbPanel);
        this.add(timePanel);
        this.add(readyQueuePanel);
        this.add(runningQueuePanel);
        this.add(waitQueuePanel);
        this.add(insPanel);
        this.add(semaphorePanel);
        this.add(pageTablePanel);
        this.add(fastTablePanel);
        
        this.setTitle("朴素可视化任务管理与虚存管理");
        this.setBackground(Color.LIGHT_GRAY);
        this.setLocation(350, 250);
        this.setSize(1200, 580);
        this.setResizable(false);
        this.setVisible(true);
	}
	
	private void initialize() {
		
		// initialize components
		cpu = new Cpu();
		memory = new Memory();
		disk = new Disk();
		exchangeArea = new ExchangeArea(disk);
		fastTable = new FastTable(3);
		mmu = new MMU();
		
		// initialize semaphores
		semaphoreNum = 4;
		semaphores = new Semaphore[semaphoreNum];
		semaphores[0] = new Semaphore(0);
		semaphores[1] = new Semaphore(0);
		semaphores[2] = new Semaphore(1);
		semaphores[3] = new Semaphore(1);
		
		// create queues
		readyQueue = new LinkedList<Pcb>();
		runningQueue = new LinkedList<Pcb>();
		waitQueue = new LinkedList<Pcb>();
		
		// create executor
		executor = new Executor(cpu, memory, disk, exchangeArea, fastTable, mmu, semaphores, readyQueue, runningQueue, waitQueue);
		
	}
	
	private void go() {
		
		// running flag
		runningFlag = true;
		startButton.setText("正在运行");
		startButton.setEnabled(false);
		fileNameTextField.setEnabled(false);
		delayRatioTextField.setEnabled(false);
		
		// time proposed
		nowTime = 0;
		
		// go thread
		new GoThread().start();
		
		// running
		while (!JobHelper.AllJobsFinished(jobs)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// running done
		runningFlag = false;
		startButton.setText("开始运行");
		startButton.setEnabled(true);
		fileNameTextField.setEnabled(true);
		delayRatioTextField.setEnabled(true);
		
		updateUI();
		JOptionPane.showMessageDialog(null, "运行完成", "提示", JOptionPane.INFORMATION_MESSAGE);
		
	}
	
	private void updateUI() {
		
		// update jcb table
		Object[][] jcbTableData = new Object[jobs.size()][4];
		for (int i = 0; i < jobs.size(); i++) {
			jcbTableData[i][0] = jobs.get(i).jobId;
			jcbTableData[i][1] = jobs.get(i).insNum;
			jcbTableData[i][2] = jobs.get(i).inTime;
			jcbTableData[i][3] = jobs.get(i).status;
		}
		Object[] jcbTableHead = {"jobId", "insNum", "inTime", "status"};
		DefaultTableModel jcbTableModel = new DefaultTableModel(jcbTableData, jcbTableHead);
		jcbTable.setModel(jcbTableModel);
		
		// update time label
		timeLabel.setText(nowTime + "ms");
		
		// update ready queue table
		Object[][] readyQueueTableData = new Object[readyQueue.size()][4];
		int i = 0;
		for (Pcb pcb : readyQueue) {
			readyQueueTableData[i][0] = pcb.pcbId;
			readyQueueTableData[i][1] = pcb.insNum;
			readyQueueTableData[i][2] = pcb.nowInsIndex;
			readyQueueTableData[i][3] = pcb.status;
			i++;
		}
		Object[] readyQueueTableHead = {"pcbId", "insNum", "nowIndex", "status"};
		DefaultTableModel readyQueueTableModel = new DefaultTableModel(readyQueueTableData, readyQueueTableHead);
		readyQueueTable.setModel(readyQueueTableModel);
		
		// update running queue table
		Object[][] runningQueueTableData = new Object[runningQueue.size()][4];
		i = 0;
		for (Pcb pcb : runningQueue) {
			runningQueueTableData[i][0] = pcb.pcbId;
			runningQueueTableData[i][1] = pcb.nowInsIndex;
			runningQueueTableData[i][2] = pcb.timePieceLeft;
			runningQueueTableData[i][3] = pcb.status;
			i++;
		}
		Object[] runningQueueTableHead = {"pcbId", "nowIndex", "timePieceLeft", "status"};
		DefaultTableModel runningQueueTableModel = new DefaultTableModel(runningQueueTableData, runningQueueTableHead);
		runningQueueTable.setModel(runningQueueTableModel);
		
		// update wait queue table
		Object[][] waitQueueTableData = new Object[waitQueue.size()][5];
		i = 0;
		for (Pcb pcb : waitQueue) {
			waitQueueTableData[i][0] = pcb.pcbId;
			waitQueueTableData[i][1] = pcb.insNum;
			waitQueueTableData[i][2] = pcb.waitTimeLeft;
			waitQueueTableData[i][3] = pcb.waitTimeCount;
			waitQueueTableData[i][4] = pcb.status;
			i++;
		}
		Object[] waitQueueTableHead = {"pcbId", "nowIndex", "waitTimeLeft", "waitTimeCount", "status"};
		DefaultTableModel waitQueueTableModel = new DefaultTableModel(waitQueueTableData, waitQueueTableHead);
		waitQueueTable.setModel(waitQueueTableModel);
		
		// update ins table
		Object[][] insTableData = {};
		int insTableSelection = -1;
		if (runningQueue.peek() != null) {
			insTableData = new Object[runningQueue.peek().insList.size()][3];
			i = 0;
			for (Instruction ins : runningQueue.peek().insList) {
				insTableData[i][0] = ins.insId;
				insTableData[i][1] = ins.ToString();
				insTableData[i][2] = ins.insLeftTime;
				i++;
			}
			insTableSelection = runningQueue.peek().nowInsIndex;
		}
		Object[] insTableHead = {"insId", "instruction", "insLeftTime"};
		DefaultTableModel insTableModel = new DefaultTableModel(insTableData, insTableHead);
		insTable.setModel(insTableModel);
		if (insTableSelection != -1) {
			insTable.setRowSelectionInterval(insTableSelection, insTableSelection);	
		}
		
		// update semaphore table
		Object[][] semaphoreTableData = new Object[semaphoreNum][2];
		for (i = 0; i < semaphoreNum; i++) {
			semaphoreTableData[i][0] = i;
			semaphoreTableData[i][1] = semaphores[i].value;
		}
		Object[] semaphoreTableHead = {"resourceId", "value"};
		DefaultTableModel semaphoreTableModel = new DefaultTableModel(semaphoreTableData, semaphoreTableHead);
		semaphoreTable.setModel(semaphoreTableModel);
		
		// update page table table
		Pcb runningPcb = runningQueue.peek();
		Object[][] pageTableTableData = {};
		if (runningPcb != null) {
			pageTableTableData = new Object[runningPcb.pageTable.length][6];
			for (i = 0; i < runningPcb.pageTable.length; i++) {
				pageTableTableData[i][0] = runningPcb.pageTable.page[i].PageId;
				pageTableTableData[i][1] = runningPcb.pageTable.page[i].BlockId;
				pageTableTableData[i][2] = runningPcb.pageTable.page[i].exchangeAreaId;
				pageTableTableData[i][3] = runningPcb.pageTable.page[i].Dwell;
				pageTableTableData[i][4] = runningPcb.pageTable.page[i].visitcount;
				pageTableTableData[i][5] = runningPcb.pageTable.page[i].changed;
			}
		}
		Object[] pageTableTableHead = {"pageId", "blockId", "exAreaId", "dwell", "vcount", "changed"};
		DefaultTableModel pageTableTableModel = new DefaultTableModel(pageTableTableData, pageTableTableHead);
		pageTableTable.setModel(pageTableTableModel);
		
		// update fast table table
		Object[][] fastTableTableData = new Object[fastTable.length][3];
		for (i = 0; i < fastTable.length; i++) {
			fastTableTableData[i][0] = fastTable.page[i].PageId;
			fastTableTableData[i][1] = fastTable.page[i].BlockId;
			fastTableTableData[i][2] = fastTable.page[i].visitcount;
		}
		Object[] fastTableTableHead = {"pageId", "blockId", "vcount"};
		DefaultTableModel fastTableTableModel = new DefaultTableModel(fastTableTableData, fastTableTableHead);
		fastTableTable.setModel(fastTableTableModel);
		
	}
	
	
	public class UpdateUIThread extends Thread {
		
		public void run() {
			while (runningFlag == true) {
				updateUI();
				try {
					Thread.sleep(new Double(10 * speedDelay).longValue());
				} catch (InterruptedException e) {
					// Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public class GoThread extends Thread {
		
		public void run() {
			while (!JobHelper.AllJobsFinished(jobs)) {
				
				System.out.println("---------- " + nowTime + "ms ----------");
				
				// check new jobs
				for (Jcb job : jobs) {
					if (job.inTime == nowTime) {
						// create process
						Primitive.Create(job, readyQueue, exchangeArea);
						
						// change job status
						job.status = Jcb.JobStatus.IN_QUEUE;
					}
				} 
				
				// handle process in running queue
				if (!runningQueue.isEmpty()) {
					
					// get pcb of running process
					Pcb runningPcb = runningQueue.peek();
					
					// get running instruction
					Instruction runningInstruction = runningPcb.insList.get(runningPcb.nowInsIndex);
					
					// check whether now instruction finished or not
					if (runningInstruction.insLeftTime == 0) {
						// now instruction finished
						
						// check whether more instructions left
						if (runningPcb.nowInsIndex + 1 >= runningPcb.insNum) {
							// no more instructions left
							
							// withdraw process
							Primitive.Withdraw(runningPcb, runningQueue, semaphores, memory);
							
							// set job finished
							runningPcb.oriJob.status = Jcb.JobStatus.FINISHED;
							System.out.println("Job " + runningPcb.oriJob.jobId + " finished.");
						} else {
							// has more instructions left
							
							// now ins index ++
							runningPcb.nowInsIndex++;
							
							// get next instruction
							Instruction nextInstruction = runningPcb.insList.get(runningPcb.nowInsIndex);
							
							// check time piece left time enough or not
							if (runningPcb.timePieceLeft < nextInstruction.insLeftTime) {
								// no enough time for next instruction
								
								// exchange out
								Primitive.ExchangeOut(runningPcb, runningQueue, readyQueue, cpu);
								
							} else {
								// has enough time for next instruction
								
								// execute instruction
								int returnCode = executor.Execute(nextInstruction, runningPcb);
								
								switch (returnCode) {
								case 3:
									// now pcb in wait queue
									break;
								case 0:
								default:
									// modify time
									System.out.println("Instruction " + nextInstruction.insId + " time left " + nextInstruction.insLeftTime + "ms, time piece left " + runningPcb.timePieceLeft + "ms.");
									nextInstruction.insLeftTime -= 10;
									runningPcb.timePieceLeft -= 10;
									break;
								}
							}
						}
					} else {
						// now instruction not finished
						
						// modify time
						System.out.println("Instruction " + runningInstruction.insId + " time left " + runningInstruction.insLeftTime + "ms, time piece left " + runningPcb.timePieceLeft + "ms.");
						runningInstruction.insLeftTime -= 10;
						runningPcb.timePieceLeft -= 10;
					}
				}
				
				// wakeup process in wait queue
				
				// pcbs can be waken up
				ArrayList<Pcb> wakenPcbs = new ArrayList<Pcb>();
				
				// select pcbs which can be waken up and modify other pcbs' time 
				for (Pcb pcb : waitQueue) {
					
					// deadlock detection
					if (pcb.waitTimeCount >= 1000) {
						// deadlock detected
						
						System.out.println("Pcb " + pcb.pcbId + " has already waited at least " + pcb.waitTimeCount + "ms, deadlock detected!");					
						
						// withdraw process
						Primitive.Withdraw(pcb, waitQueue, semaphores, memory);
						
						// re-add to ready queue
						Primitive.Create(pcb.oriJob, readyQueue, exchangeArea);
					}
					
					// check whether can be waken up
					if (pcb.waitTimeLeft == 0) {
						// can be waken up
						
						// add to list
						wakenPcbs.add(pcb);
					} else if (pcb.waitTimeLeft == -1) {
						// blocked due to semaphore
						
						// modify wait time count
						System.out.println("Pcb " + pcb.pcbId + " has already waited " + pcb.waitTimeCount + "ms, continue waiting...");					
						pcb.waitTimeCount += 10;
					} else {
						// cannot be waken up
						
						// modify wait time left
						System.out.println("Pcb " + pcb.pcbId + " wait time left " + pcb.waitTimeLeft + "ms.");
						pcb.waitTimeLeft -= 10;
					}
				}
				
				// wake pcbs up
				for (Pcb pcb : wakenPcbs) {
					
					// wake up pcb
					Primitive.WakeUp(pcb, waitQueue, readyQueue);
					
				}
				
				// select in new process
				if (runningQueue.isEmpty() && !readyQueue.isEmpty()) {
					
					// get first pcb in ready queue
					Pcb firstPcb = readyQueue.peek();
				
					// check memory space
					int requiredSpace = (firstPcb.pageTable.length - 5) < 5 ? 5 : (firstPcb.pageTable.length - 5);
					if (memory.CanAllocate(requiredSpace)) {
						// memory has enough space
						
						// select in first pcb
						Primitive.SelectIn(firstPcb, readyQueue, runningQueue, cpu, exchangeArea, memory);
						
						// check whether more instructions left
						if (firstPcb.nowInsIndex >= firstPcb.insNum) {
							// no more instructions left
							
							// withdraw process
							Primitive.Withdraw(firstPcb, runningQueue, semaphores, memory);
							
							// set job finished
							firstPcb.oriJob.status = Jcb.JobStatus.FINISHED;
							System.out.println("job " + firstPcb.oriJob.jobId + " finished.");
						} else {
							// has more instructions left
							
							// get first instruction
							Instruction firstInstrruction = firstPcb.insList.get(firstPcb.nowInsIndex);
							
							// check time piece left time enough or not
							if (firstPcb.timePieceLeft < firstInstrruction.insLeftTime) {
								// no enough time for next instruction
								
								// exchange out
								Primitive.ExchangeOut(firstPcb, runningQueue, readyQueue, cpu);
								
							} else {
								// has enough time for next instruction
								
								// execute instruction
								int returnCode = executor.Execute(firstInstrruction, firstPcb);
								
								switch (returnCode) {
								case 3:
									// now pcb in wait queue
									break;
								case 0:
								default:
									// modify time
									System.out.println("Instruction " + firstInstrruction.insId + " time left " + firstInstrruction.insLeftTime + "ms, time piece left " + firstPcb.timePieceLeft + "ms.");
									firstInstrruction.insLeftTime -= 10;
									firstPcb.timePieceLeft -= 10;
									break;
								}
							}
						}
					} else {
						// memory has not enough space
						
						// turn to the last in ready queue
						readyQueue.remove(firstPcb);
						readyQueue.offer(firstPcb);
					}
					
				}
				
				System.out.println("---------- end ----------");
				System.out.println();
				
				// modify now time
				nowTime += 10;
				
				// make time delay
				try {
					Thread.sleep(new Double(10 * speedDelay).longValue());
				} catch (InterruptedException e) {
					// Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
