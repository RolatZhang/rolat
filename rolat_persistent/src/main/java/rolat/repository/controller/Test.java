package rolat.repository.controller;

public final class Test {
    int a=1;

    public static void main(String[] args) {
        String result = Integer.toBinaryString(8);
             int r = Integer.parseInt(result);
             System.out.println(r);
             System.out.println(result);
        System.out.println((8&1)==1);
    }
}
