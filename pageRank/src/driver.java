public class driver {

    public static void main(String[] args) throws Exception {
        pageRank multiplication = new pageRank();
        calculate sum = new calculate();
        String transitionPath = args[0];
        String prPath = args[1];
        String tempResultPath = args[2];
        String newPrPath = args[3];
        int times = Integer.parseInt(args[4]);
        for(int i = 0; i < times; i++){
            String[] arg1 = {transitionPath, prPath, tempResultPath};
            String[] arg2 = {tempResultPath, newPrPath};
            multiplication.main(arg1);
            sum.main(arg2);
            prPath = newPrPath;
            tempResultPath = tempResultPath + "" + i;
            newPrPath = newPrPath + "" + i;
        }
    }
}