class Main{

    public static void main(String args[]){
        Race race = new Race(25);

        Horse apples = new Horse('♞', "Apples", 0.4);
        Horse butter = new Horse('♘', "Butter", 0.5);
        Horse chevy = new Horse('♕', "Chevy", 0.6);

        race.addHorse(apples, 1);
        race.addHorse(butter, 2);
        race.addHorse(chevy, 3);

        race.startRace();
    }
}