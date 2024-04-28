public class App {
    public static void main(String[] args) throws Exception {
        System.out.println(
//                chess.Square$package$Square$.MODULE$.fromKey("A1") // package chess.Square$package$Square$ does not exist
        );

        Class<?> cls = Class.forName("chess.Square$package$Square$");
        Object obj = cls.getField("MODULE$").get(null);
        System.out.println(
                cls.getMethod("fromKey", String.class).invoke(obj, "A1")
        );
    }
}
