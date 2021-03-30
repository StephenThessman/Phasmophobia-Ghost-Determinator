/*
 * Phasmophobia Ghost Eliminator
 * Version: v1.2.666
 * 9/1/2021
 * Creator: Stephen Thessman
 * Description: This program is made for the game Phasmophobia. My friend wrote and excel spreadsheet and I converted
 * it into this Java program. All logic came from him and I simply turned it into code. There will be some not so clean
 * coding parts as I am a second year SWEN student doing this in my Summer break to continue coding.
 * */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class PhasmophobiaGhostMain implements ActionListener {

    // Constants
    public static final int HEIGHT = 450;
    public static final int WIDTH = 650;
    public static final int NUM_GHOST = 12;
    public static final int NUM_EVIDENCE = 6;
    public static final String[] GHOST_NAMES = {"Spirit", "Wraith", "Phantom", "Poltergeist", "Banshee", "Jinn", "Mare", "Revenant", "Shade", "Demon", "Yurei", "Oni"};
    public static final String[] EVIDENCE_NAME = {"Fingerprints", "Ghost Writing", "Spirit Box", "Freezing Temperatures", "Ghost Orbs", "EMF Level 5"};
    public static final Color DARK_GREEN = new Color(34, 120, 50);

    //fields
    private final int[][] ghostLogic = new int[NUM_GHOST][NUM_EVIDENCE];
    private final boolean[][] ghostTruth = new boolean[NUM_GHOST][NUM_EVIDENCE];
    private final int[] ghostOptional = new int[NUM_GHOST];

    private final boolean[][] evidencePossiblePart1 = new boolean[NUM_GHOST][NUM_EVIDENCE];
    private final boolean[] evidenceSummaryPart1 = new boolean[NUM_EVIDENCE];

    private final boolean[][] evidencePossiblePart2 = new boolean[NUM_GHOST][NUM_EVIDENCE];
    private final boolean[] evidenceSummaryPart2 = new boolean[NUM_EVIDENCE];

    private final int[] playerLogic = new int[NUM_EVIDENCE];
    private final boolean[] ghostPossible = new boolean[NUM_GHOST];

    private final Map<String, List<JRadioButton>> evidenceButtons = new TreeMap<>();
    private final Map<String, List<JRadioButton>> ghostButtons = new TreeMap<>();

    private final List<JLabel> evi = new ArrayList<>(); // List of the evidence JLabels
    private final List<JLabel> ghostLabs = new ArrayList<>(); // List of ghost name JLabels
    private final JLabel confirmedGhost = new JLabel("Select evidence to determine ghost");

    /** public constructor for Phasmophobia Ghost Calculator. Requires no inputs*/
    public PhasmophobiaGhostMain(){

        // populate all the logic arrays
        populateGhostLogic();
        populateArrayLogic();
        populateLabs(); // create the ghost labels

        // Creating the JFrame for the program
        JFrame phasmo = new JFrame("Phasmophobia Ghost Eliminator: 666");
        phasmo.setLayout(new GridLayout(2, 3, 5, 5));
        phasmo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        phasmo.setMinimumSize(new Dimension(WIDTH, HEIGHT)); // minimum size is that which fits all info nicely
        phasmo.setSize(WIDTH, HEIGHT); // default size


        JPanel evidence = new JPanel();
        evidence.setLayout(new GridLayout(6, 2, -5, 0));
        evidence = createEvidenceButtons(evidence); // overwrite the evidence panel with the updated one with evidence on it

        JPanel ghosts = new JPanel();
        ghosts.setLayout(new GridLayout(1, 2));
        ghosts = createGhostButtons(ghosts); // overwrite the ghosts panel with the updated one with ghosts on it

        // the reset button
        JButton reset = new JButton("Reset");
        reset.setActionCommand("Reset");
        reset.addActionListener(this);

        // a new panel for the reset buttn roughly centered in it's jFrame coords
        JPanel res = new JPanel();
        res.setBorder(BorderFactory.createEmptyBorder(80,10,10,10));
        res.add(reset);

        // add all panels to main frame
        phasmo.add(evidence);
        phasmo.add(ghosts);
        phasmo.add(res);
        phasmo.add(confirmedGhost);

        phasmo.setVisible(true);
    }

    /** Creates the evidence buttons with a label, yes/no/na radio button. Accepts a JPanel and returns the same JPanel*/
    private JPanel createEvidenceButtons(JPanel evidence){
        for(int i = 0; i < 6; i++){
            String ev = EVIDENCE_NAME[i];
            JLabel lab = new JLabel(ev);
            evidence.add(lab);
            evi.add(lab);

            JPanel buttons = new JPanel();
            buttons.setLayout(new FlowLayout());

            JRadioButton yes = new JRadioButton("Yes");
            yes.setActionCommand(ev + ":yes");
            yes.addActionListener(this);
            buttons.add(yes);

            JRadioButton no = new JRadioButton("No");
            no.setActionCommand(ev + ":no");
            no.addActionListener(this);
            buttons.add(no);

            JRadioButton na = new JRadioButton("N/A", true);
            na.setActionCommand(ev + ":na");
            na.addActionListener(this);
            buttons.add(na);

            evidence.add(buttons);

            //  adds the radio buttons to a map so their value can be retrieved by other methods
            List<JRadioButton> radio = new ArrayList<>();
            radio.add(yes);
            radio.add(no);
            radio.add(na);
            evidenceButtons.put(ev, radio);

            // new button group so only one button is selected
            ButtonGroup evGroup = new ButtonGroup();
            evGroup.add(yes);
            evGroup.add(no);
            evGroup.add(na);
        }
        return evidence;
    }

    /** Creates the ghost buttons with a label, yes/no/na radio button. Accepts a JPanel and returns the same JPanel*/
    private JPanel createGhostButtons(JPanel ghosts){
        // panel for ghost names
        JPanel text = new JPanel();
        text.setLayout(new GridLayout(12, 1));

        // panel for all the radio buttons
        JPanel allButtons = new JPanel();
        allButtons.setLayout(new GridLayout(12, 1));

        for(JLabel lab : ghostLabs){

            text.add(lab);
            // panel for each set of radio buttons per ghost
            JPanel buttons = new JPanel();
            buttons.setLayout(new GridLayout(1, 3));

            JRadioButton yes = new JRadioButton("Yes");
            yes.setActionCommand(lab.getText() + ":yes");
            yes.addActionListener(this);
            buttons.add(yes);

            JRadioButton no = new JRadioButton("No");
            no.setActionCommand(lab.getText() + ":no");
            no.addActionListener(this);
            buttons.add(no);

            JRadioButton na = new JRadioButton("N/A", true);
            na.setActionCommand(lab.getText() + ":na");
            na.addActionListener(this);
            buttons.add(na);

            allButtons.add(buttons);

            // map to store all radio buttons to each ghost type so can be accessed later.
            List<JRadioButton> radio = new ArrayList<>();
            radio.add(yes);
            radio.add(no);
            radio.add(na);
            ghostButtons.put(lab.getText(), radio);

            // button group for radio buttons so only one can be selectec
            ButtonGroup ghostGroup = new ButtonGroup();
            ghostGroup.add(yes);
            ghostGroup.add(no);
            ghostGroup.add(na);
        }
        ghosts.add(text);
        ghosts.add(allButtons);

        return ghosts;
    }

    /** fills the Ghost logic 2d array with the ghost logic*/
    private void populateGhostLogic(){
        for(int j = 0; j < NUM_GHOST; j++) {
            for (int i = 0; i < NUM_EVIDENCE; i++) {
                // Spirit evidence
                if (j == 0) {
                    if (i < 3) ghostLogic[j][i] = 1;
                    else ghostLogic[j][i] = 0;

                    // Wraith evidence
                } else if (j == 1) {
                    if (i == 0 || i == 2 || i == 3) ghostLogic[j][i] = 1;
                    else ghostLogic[j][i] = 0;

                    // Phantom evidence
                } else if (j == 2) {
                    if (i < 3) ghostLogic[j][i] = 0;
                    else ghostLogic[j][i] = 1;

                    // Poltergeist evidence
                } else if (j == 3) {
                    if (i % 2 == 0) ghostLogic[j][i] = 1;
                    else ghostLogic[j][i] = 0;

                    // Banshee evidence
                } else if (j == 4) {
                    if (i == 0 || i == 3 || i == 5) ghostLogic[j][i] = 1;
                    else ghostLogic[j][i] = 0;

                    // Jinn evidence
                } else if (j == 5){
                    if (i == 2 || i == 4 || i == 5) ghostLogic[j][i] = 1;
                    else ghostLogic[j][i] = 0;

                    // Mare evidence
                } else if (j == 6){
                    if (i >= 2 && i <= 4) ghostLogic[j][i] = 1;
                    else ghostLogic[j][i] = 0;

                    // Revenant evidence
                } else if (j == 7){
                    if (i >= 2 && i <= 4) ghostLogic[j][i] = 0;
                    else ghostLogic[j][i] = 1;

                    // Shade evidence
                } else if (j == 8){
                    if (i == 1 || i == 4 || i == 5) ghostLogic[j][i] = 1;
                    else ghostLogic[j][i] = 0;

                    // Demon evidence
                } else if (j == 9){
                    if (i >= 1 && i <= 3) ghostLogic[j][i] = 1;
                    else ghostLogic[j][i] = 0;

                    // Yurei evidence
                } else if (j == 10){
                    if (i == 1 || i == 3 || i == 4) ghostLogic[j][i] = 1;
                    else ghostLogic[j][i] = 0;

                    // Oni evidence
                } else {
                    if (i == 1 || i == 2 || i == 5) ghostLogic[j][i] = 1;
                    else ghostLogic[j][i] = 0;

                }

                ghostTruth[j][i] = true;
            }
        }

    }

    /** fills all the arrays with their respected values*/
    private void populateArrayLogic(){
        Arrays.fill(ghostOptional, 1);
        Arrays.fill(ghostPossible, true);
        Arrays.fill(playerLogic, -1);
    }

    /** populates the Ghost Labels array with the labels of the ghost names*/
    private void populateLabs(){
        ghostLabs.clear();
        for(int ghost = 0; ghost < NUM_GHOST; ghost++){
            ghostLabs.add(new JLabel(GHOST_NAMES[ghost]));
        }
        setAlLabelColorGreen();
    }

    /** set the colour of all ghost name labels to green*/
    private void setAlLabelColorGreen(){
        for(JLabel lab : ghostLabs){
            lab.setForeground(DARK_GREEN);
        }
    }

    /** Handle button events to add to player evidence */
    public void actionPerformed(ActionEvent ae){
        String ev = ae.getActionCommand();

        if(ev.equalsIgnoreCase("Reset")){ // when the reset button is pressed
            populateArrayLogic(); // reset the arrays
            setAlLabelColorGreen(); // change all ghost labels colour back to green

            // selects the N/A button as the default when reset
            for(Map.Entry<String, List<JRadioButton>> butt : evidenceButtons.entrySet()){
                butt.getValue().get(2).setSelected(true);
            }
            for(Map.Entry<String, List<JRadioButton>> butt : ghostButtons.entrySet()){
                butt.getValue().get(2).setSelected(true);
            }
            // change the evidence labels colour to black
            for(JLabel lab : evi){
                lab.setForeground(Color.BLACK);
            }
            // reset the ghost result text
            confirmedGhost.setText("Select evidence to determine ghost");

        } else{

            int colon = ev.indexOf(":"); // used to split the ev string
            String evidence = ev.substring(0, colon);
            String buttonSelected = ev.substring(colon + 1); // which radio button ended up being selected

            // action used to determine if something is true/false/not applicable
            int action = 0;
            if(buttonSelected.equals("yes")) action = 1;
            else if (buttonSelected.equals("na")) action = -1;

            determinePlayerAction(evidence, action);

            ghostType();
        }
    }

    /** Determines what button the player has selected and then updates the respected playerLogic or ghostOptional value*/
    private void determinePlayerAction(String evidence, int action){
        // this if statement is used to determine what the player action was. It then sets the evidence/ghost a value
        if(evidence.equalsIgnoreCase("Fingerprints")) playerLogic[0] = action;
        else if (evidence.equalsIgnoreCase("Ghost Writing")) playerLogic[1] = action;
        else if (evidence.equalsIgnoreCase("Spirit Box")) playerLogic[2] = action;
        else if (evidence.equalsIgnoreCase("Freezing Temperatures")) playerLogic[3] = action;
        else if (evidence.equalsIgnoreCase("Ghost Orbs")) playerLogic[4] = action;
        else if (evidence.equalsIgnoreCase("EMF Level 5")) playerLogic[5] = action;
        else if (evidence.equalsIgnoreCase("Spirit")) ghostOptional[0] = action + 2;
        else if (evidence.equalsIgnoreCase("Wraith")) ghostOptional[1] = action + 2;
        else if (evidence.equalsIgnoreCase("Phantom")) ghostOptional[2] = action + 2;
        else if (evidence.equalsIgnoreCase("Poltergeist")) ghostOptional[3] = action + 2;
        else if (evidence.equalsIgnoreCase("Banshee")) ghostOptional[4] = action + 2;
        else if (evidence.equalsIgnoreCase("Jinn")) ghostOptional[5] = action + 2;
        else if (evidence.equalsIgnoreCase("Mare")) ghostOptional[6] = action + 2;
        else if (evidence.equalsIgnoreCase("Revenant")) ghostOptional[7] = action + 2;
        else if (evidence.equalsIgnoreCase("Shade")) ghostOptional[8] = action + 2;
        else if (evidence.equalsIgnoreCase("Demon")) ghostOptional[9] = action + 2;
        else if (evidence.equalsIgnoreCase("Yurei")) ghostOptional[10] = action + 2;
        else if (evidence.equalsIgnoreCase("Oni")) ghostOptional[11] = action + 2;
    }

    /** Determines what ghosts are currently possible */
    private void ghostType(){
        String confirmedGhostName = ""; // name of the ghost that has been determined

        boolean[] optionalGhostArray = new boolean[NUM_GHOST]; // determines if a ghost has been selected yes or no

        // All ghosts
        for(int ghost = 0; ghost < ghostPossible.length; ghost++){
            for(int evidence = 0; evidence < playerLogic.length; evidence++) {

                // the truth table that has been supplied by Soyfya
                boolean input = playerLogic[evidence] == -1 || playerLogic[evidence] == ghostLogic[ghost][evidence];
                ghostTruth[ghost][evidence] = input; // set the truth in the final logic array
            }
            boolean optionalGhost = true;

            // used to determine if the player has selected yes or no on the ghosts themselves
            for (int optional = 0; optional < ghostOptional.length; optional++) {
                // if optionalGhost is false; input false to array and break. prevents overwrites to true
                if(!optionalGhost) {
                    optionalGhostArray[ghost] = false;
                    break;
                }
                else {
                    if (ghostOptional[optional] == 3) { // ghost is selected yes
                        if (optional != ghost) optionalGhost = false; // the current ghost is not the ghost that has been selected yes for

                    } else if (ghostOptional[optional] == 2) { // ghost is selected no
                        optionalGhost = optional != ghost; // current ghost is not current optionalGhost for selected no
                    }
                    optionalGhostArray[ghost] = optionalGhost;
                }
            }
        }

        int ghostCount = 0;
        // change the ghost lab colour if ghost is still possible and evidence colour
        for(int ghost = 0; ghost < ghostPossible.length; ghost++){

            // the truth provided by Soyfya for whether the ghost is possible or not
            ghostPossible[ghost] = ghostTruth[ghost][0] && ghostTruth[ghost][1] && ghostTruth[ghost][2] &&
                                    ghostTruth[ghost][3] && ghostTruth[ghost][4] && ghostTruth[ghost][5] && optionalGhostArray[ghost];

            JLabel ghostLab = ghostLabs.get(ghost);

            // if the ghost is still possible sets the colour to green and increases the ghost counter if not sets it to red colour
            if(ghostPossible[ghost] && ghostOptional[ghost] != 2) {
                ghostLab.setForeground(DARK_GREEN);
                ghostCount++;
                confirmedGhostName = ghostLab.getText();
            }
            else ghostLab.setForeground(Color.red);
        }

        // determines if you only have one ghost possible and then informs you of the result
        if(ghostCount == 1){
            confirmedGhost.setText("Your ghost is " + confirmedGhostName);

        } else if(ghostCount == 0){
            confirmedGhost.setText("No possible Ghost");

        } else{
            confirmedGhost.setText("Select evidence to determine ghost");
        }

        evidencePossible();
        changeEvidenceColour();

    }

    /** Changes the colour of the evidence dependant on whether there are ghosts available that still have the evidence*/
    private void changeEvidenceColour(){
        for(int evi = 0; evi < playerLogic.length; evi++){
            JLabel evideLab = this.evi.get(evi); // the working button from the evidence list

            if(evidenceSummaryPart2[evi]){
                evideLab.setForeground(DARK_GREEN); // possible evidence

            } else if (!evidenceSummaryPart1[evi]){
                evideLab.setForeground(Color.RED); // not possible evidence

            } else {
                evideLab.setForeground(Color.black); // set back to normal
            }
        }
    }

    /** Determines if evidence is still possible*/
    private void evidencePossible(){

        for(int evidence = 0; evidence < playerLogic.length; evidence++) {
            boolean isPossiblePart1 = false;
            boolean isPossiblePart2 = true;
            // part one of Soyfya logic table to determine if evidence is still valid
            for (int ghost = 0; ghost < ghostPossible.length; ghost++) {
                if (ghostPossible[ghost] && (ghostLogic[ghost][evidence] == 1)) {
                    evidencePossiblePart1[ghost][evidence] = true;
                    isPossiblePart1 = true;
                } else {
                    evidencePossiblePart1[ghost][evidence] = false;
                }
            }
            evidenceSummaryPart1[evidence] = isPossiblePart1;

            // part two of Soyfya logic table to determine if evidence is still valid
            for (int ghost = 0; ghost < ghostPossible.length; ghost++) {
                // (!J$19 || (J20 && J$19) &&
                // =AND(OR(NOT(J$19),AND(J20,J$19)),OR($J20:$U20))
                evidencePossiblePart2[ghost][evidence] = (!ghostPossible[ghost] || (evidencePossiblePart1[ghost][evidence] && ghostPossible[ghost])) && (evidenceSummaryPart1[evidence]);
            }
            for(int ghost = 0; ghost < ghostPossible.length; ghost++) {
                if(!evidencePossiblePart2[ghost][evidence]) {
                    isPossiblePart2 = false;
                    break;
                }
            }
            evidenceSummaryPart2[evidence] = isPossiblePart2;
        }
    }

    /** Program Main*/
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PhasmophobiaGhostMain::new);
    }

}
