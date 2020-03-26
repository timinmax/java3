public abstract class Fruit {
    protected String name;
    protected float weight;
    protected static float DEFAULT_WEIGHT = 1.0f;

     public String getName(){
         return name;
     };

    public static float getDefaultWeight(){
        return DEFAULT_WEIGHT;
    };

    public float getWeight() {
        return weight;
    }

}
