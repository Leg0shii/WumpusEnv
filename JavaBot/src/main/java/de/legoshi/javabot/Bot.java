package main.java.de.legoshi.javabot;

import java.util.Arrays;

/**
 * @author Benjamin Müller
 * @author Julia Koch
 * @author Yazar Strulik
 */

/* TODO

*/



public class Bot {

    private FileHelper fileHelper;
    //gameState = "C;[self];[x,y];hasgold;escaped;alive" = C;[START,PLAYER];[0.0,0.0];false;false;true
    public String gameState;
    //gameArray[1] = "[START,PLAYER]"
    public String[] gameArray;
    public String command;

    public String stateString;
    public String[] stateArray;

    //Width und Height werden beim execute gesetzt
    public int width;
    public int height;

    //Fliehend
    //public String runFromWumpus;
    //public int fleeing = 0;

    //Felder für die Erkennung
    public int[][] visited;
    public int[][] prob;
    public boolean[][] safe;

    public String up = "B;UP;false;false;false";
    public String down = "B;DOWN;false;false;false";
    public String left = "B;LEFT;false;false;false";
    public String right = "B;RIGHT;false;false;false";
    //public String scream = "B;NOTHING;true;false;false";
    public String pickup = "B;NOTHING;false;true;false";
    public String climb = "B;NOTHING;false;false;true";


    //Initialisierungsvariable
    public int s0 = 0;
    int x; //X Position des Bots
    int y; //Y Position des Bots
    int tx = 100; //X in Zeit t-1
    int ty = 100; //Y in Zeit t-1

    public Bot(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }


    public void constructField() {
        this.visited = new int[width * 3][height * 3];
        this.prob = new int[width * 3][height * 3];
        this.safe = new boolean[width * 3][height * 3];
    }


//Dangerfunktion: Setzt Gefährlichkeitswerte für bestimmte Felder außer diese wurden schon besucht

    public boolean arraycontains(String s){
        for (String value : stateArray) {
            if (value.equals(s)) {
                return true;
            }
        }
      return false;
    }

    public void danger(int fx, int fy) {
        if (!safe[width + fx + 1][height + fy]) {
            prob[width + fx + 1][height + fy] += 1;
        }
        if (!safe[width + fx - 1][height + fy]) {
            prob[width + fx - 1][height + fy] += 1;
        }
        if (!safe[width + fx][height + fy + 1]) {
            prob[width + fx][height + fy + 1] += 1;
        }
        if (!safe[width + fx][height + fy - 1]) {
            prob[width + fx][height + fy - 1] += 1;
        }

    }

    public void marksafe(int fx, int fy) {
      safe[width + fx][height + fy - 1] = true;
      safe[width + fx][height + fy + 1] = true;
      safe[width + fx - 1][height + fy] = true;
      safe[width + fx + 1][height + fy] = true;
      safe[width + fx][height + fy] = true;
    }

    public String flee(int fx, int fy){
      int a = 1000; //rechts
      int b = 1000; //links
      int c = 1000; //oben
      int d = 1000; //unten
      if (visited[width + fx + 1][height + fy]>0) {
          a = visited[width + fx + 1][height + fy];
      }
      if (visited[width + fx - 1][height + fy]>0) {
          b = visited[width + fx - 1][height + fy];
      }
      if (visited[width + fx][height + fy + 1]>0) {
          c = visited[width + fx][height + fy + 1];
      }
      if (visited[width + fx][height + fy - 1]>0) {
          d = visited[width + fx][height + fy - 1];
      }

      if (arraycontains("WALL_RIGHT")) {
        a = 1001;
      }
      if (arraycontains("WALL_LEFT")) {
        b = 1001;
      }
      if (arraycontains("WALL_TOP")) {
        c = 1001;
      }
      if (arraycontains("WALL_BOTTOM")) {
        d = 1001;
      }
      int e = getMin(a, b, c, d);
      fileHelper.log("a = " +a);
      fileHelper.log("b = " +b);
      fileHelper.log("c = " +c);
      fileHelper.log("d = " +d);
      fileHelper.log("e = " +e);

      if (a == e) {
          x += 1;
          return right;
      }
      if (b == e) {
          x -= 1;
          return left;
      }
      if (c == e) {
          y += 1;
          return up;
      }
      if (d == e) {
          y -= 1;
          return down;
      }
      fileHelper.log("Fehler beim Decisionmaking");
      return null; //Ersetzen
    }

//Decisionfunktion: Entscheidet sich für das Feld mit der niedrigsten Gefährlichkeit

    public String decision(int fx, int fy) {
        int a = 1000; //rechts
        int b = 1000; //links
        int c = 1000; //oben
        int d = 1000; //unten
        if (visited[width + fx + 1][height + fy]==0 && safe[width + fx + 1][height + fy]) {
            a = prob[width + fx + 1][height + fy];
        }
        if (visited[width + fx - 1][height + fy]==0 && safe[width + fx - 1][height + fy]) {
            b = prob[width + fx - 1][height + fy];
        }
        if (visited[width + fx][height + fy + 1]==0 && safe[width + fx][height + fy + 1]) {
            c = prob[width + fx][height + fy + 1];
        }
        if (visited[width + fx][height + fy - 1]==0 && safe[width + fx][height + fy - 1]) {
            d = prob[width + fx][height + fy - 1];

            if (arraycontains("WALL_RIGHT")) {
              a = 1001;
            }
            if (arraycontains("WALL_LEFT")) {
              b = 1001;
            }
            if (arraycontains("WALL_TOP")) {
              c = 1001;
            }
            if (arraycontains("WALL_BOTTOM")) {
              d = 1001;
            }

        }
        int e = getMin(a, b, c, d);
        fileHelper.log("a = " +a);
        fileHelper.log("b = " +b);
        fileHelper.log("c = " +c);
        fileHelper.log("d = " +d);
        fileHelper.log("e = " +e);
        /*
        if (visited[width + fx + 1][height + fy]>=3){
          visited[width + fx + 1][height + fy] = 0;
        }
        if (visited[width + fx - 1][height + fy]>=3){
          visited[width + fx - 1][height + fy] = 0;
        }
        if (visited[width + fx][height + fy + 1]>=3){
          visited[width + fx][height + fy + 1] = 0;
        }
        if (visited[width + fx][height + fy - 1]>=3){
          visited[width + fx][height + fy - 1] = 0;
        }
        */

        if (a == e) {
            x += 1;
            return right;
        }
        if (b == e) {
            x -= 1;
            return left;
        }
        if (c == e) {
            y += 1;
            return up;
        }
        if (d == e) {
            y -= 1;
            return down;
        }
        fileHelper.log("Fehler beim Decisionmaking");
        return null; //Ersetzen
    }

    private int getMin(int a, int b, int c, int d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }

    public void execute() {
        this.gameArray = gameState.split(";");
        this.stateString = gameArray[1].replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s","");
        this.stateArray = stateString.split(",");

        if (s0 == 0) {
            constructField();
            s0 += 1;
            x = 0;
            y = 0;
        }

        visited[width + x][height + y] += 1;

        if (visited[width + x][height + y]>=2 || visited[width + x + 1][height + y]>=2 || visited[width + x - 1][height + y]>=2 || visited[width + x - 1][height + y - 1]>=2 || visited[width + x][height + y + 1]>=2){
          visited[width + x][height + y]=0;
          visited[width + x + 1][height + y]=0;
          visited[width + x - 1][height + y]=0;
          visited[width + x][height + y + 1]=0;
          visited[width + x][height + y - 1]=0;
        }
        //-----------nicht mehr benötigt weil wir ja vom Stench verfolgt werden--------------
        /*
        if ( fleeing <= 3 && fleeing > 0){

          command = runFromWumpus;
          fleeing += 1;
        }
        if ( fleeing > 3 ){
          fleeing = 0;
        }*/
        //Wenn Gold und am Eingangs- bzw. Ausgangspunkt -> rausklettern
        if (arraycontains("GOLD") && x == 0 && y == 0){

            command = climb;
            //Wenn Wind -> Felder mit Gefahr markieren
        } else if (arraycontains("WIND")) {

            danger(x, y);
            command = decision(x, y);
            //Wenn Gestank -> Felder mit Gefahr markieren
        } else if (arraycontains("STENCH")) {

            danger(x, y);
            command = flee(x, y);
            //-------------auch nicht mehr benötigt weil wir ja vom Stench verfolgt werden--------
            /*
            runFromWumpus = command;
            fleeing = 1;
            */
            //Wenn Gold -> Gold aufheben
        } else if (arraycontains("GOLD")) {

            command = pickup;

        } else {
            marksafe(x, y);
            command = decision(x, y);

        }
        //Weg mit wenigster Gefahr gehen

        tx = x;
        ty = y;

        fileHelper.log("X = " + x);
        fileHelper.log("Y = " + y);

        fileHelper.log(Arrays.toString(stateArray));
    }

}
