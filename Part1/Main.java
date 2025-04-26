class Main{

    public static void main(String args[]){
        Race race = new Race(25);

        Horse apple = new Horse('♞', "Apple", 0.4);
        Horse bumblebee = new Horse('♘', "Bumblebee", 0.5);
        Horse chevy = new Horse('♞', "Chevy", 0.6);

        race.addHorse(apple, 1);
        race.addHorse(bumblebee, 2);
        race.addHorse(chevy, 3);

        race.startRace();
    }
}