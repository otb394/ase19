public abstract class TblObject {
    private static int noOfObjects = 0;
    protected int oid;

    public TblObject() {
        oid = ++noOfObjects;
    }

    public static void reset() {
        noOfObjects = 0;
    }
}
