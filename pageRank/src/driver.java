public class driver {

    public static void main(String[] args) throws Exception {
        pageRank multiplication = new pageRank();
        calculate sum = new calculate();
        String transitionPath = args[0];
        String prPath = args[1];
        String tempResultPath = args[2];
        String finalResultPath = args[3];
        String[] arg1 = {transitionPath, prPath, tempResultPath};
        String[] arg2 = {tempResultPath, finalResultPath};
        multiplication.main(arg1);
        sum.main(arg2);
    }
}