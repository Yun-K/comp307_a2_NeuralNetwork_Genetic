package part1;

import java.util.Arrays;
import java.util.List;

public class a2Part1 {

    public static void main(String[] _ignored) {
        String filePath_train = "penguins307-train.csv";
        String filePath_test = "penguins307-test.csv";

        // String filePath_train =
        // "/Users/11973/git/comp307_a2_yun/comp307_a2Yun/ass2_files/part1/penguins307-train.csv";
        // String filePath_test =
        // "/Users/11973/git/comp307_a2_yun/comp307_a2Yun/ass2_files/part1/penguins307-test.csv";

        List<String[]> lines = Util.getLines(filePath_train);
        String[] header = lines.remove(0);//
        String[] labels = Util.getLabels(lines);// the class of the Pengein(i.e.
                                                // Adelie,Gentoo,)

        double[][] instances = Util.getData(lines);

        // scale features to [0,1] to improve training
        Rescaler rescaler = new Rescaler(instances);
        rescaler.rescaleData(instances);
        System.out.println(Arrays.deepToString(instances));

        // We can"t use strings as labels directly in the network, so need to do some
        // transformations
        LabelEncoder label_encoder = new LabelEncoder(labels);
        // encode "Adelie" as 1, "Chinstrap" as 2, "Gentoo" as 3
        int[] integer_encoded = label_encoder.intEncode(labels);

        // encode 1 as [1, 0, 0], 2 as [0, 1, 0], and 3 as [0, 0, 1] (to fit with our network
        // outputs!)
        int[][] onehot_encoded = label_encoder.oneHotEncode(labels);

        // Parameters. As per the handout.
        int n_in = 4, n_hidden = 2, n_out = 3;
        double learning_rate = 0.2;

        double[][] initial_hidden_layer_weights = new double[][] {
                { -0.28, -0.22 }, { 0.08, 0.20 }, { -0.30, 0.32 }, { 0.10, 0.01 } };

        double[][] initial_output_layer_weights = new double[][] {
                { -0.29, 0.03, 0.21 }, { 0.08, 0.13, -0.36 } };

        NeuralNetwork nn = new NeuralNetwork(n_in, n_hidden, n_out, initial_hidden_layer_weights,
                initial_output_layer_weights, learning_rate);

        System.out.printf(
                "First instance has label %s, which is %d as an integer, and %s as a list of outputs.\n",
                labels[0], integer_encoded[0], Arrays.toString(onehot_encoded[0]));

        // need to wrap it into a 2D array
        int[] instance1_prediction = nn.predict(new double[][] { instances[0] });
        String instance1_predicted_label;
        if (instance1_prediction[0] == -1) {
            // This should never happen once you have implemented the feedforward.
            instance1_predicted_label = "???";
        } else {
            instance1_predicted_label = label_encoder.inverse_transform(instance1_prediction[0]);
        }
        System.out
                .println("------------------------\nPredicted label for the first instance is: "
                         + instance1_predicted_label);
        System.out.println("\t\twhich is: " + labels[0].equals(instance1_predicted_label) + "\n");

        // TODO: Perform a single backpropagation pass using the first instance only.
        // (In other words, train with 1 instance for 1 epoch!).
        // Hint: you will need to first get the weights from a forward pass.
        double[][] forwardPassOutputs = nn.forward_pass(instances[0]);
        // one Epoch:using all training instances from Training Sets to train once, including
        // forwad && backward
        double[][][] oneEpoch = nn.backward_propagate_error(instances[0],
                forwardPassOutputs[0], forwardPassOutputs[1],
                integer_encoded[0]);
        nn.update_weights(oneEpoch[0], oneEpoch[1]);

        System.out.println("Weights after performing BP for first instance only:");
        System.out
                .println("Hidden layer weights:\n" + Arrays.deepToString(nn.hidden_layer_weights));
        System.out
                .println("Output layer weights:\n" + Arrays.deepToString(nn.output_layer_weights));

        // if (1 == 1) {
        // return;
        // }

        // TODO: Train for 100 epochs, on all instances.
        nn.train(instances, integer_encoded, 100);

        System.out.println("\nAfter training:");
        System.out
                .println("Hidden layer weights:\n" + Arrays.deepToString(nn.hidden_layer_weights));
        System.out
                .println("Output layer weights:\n" + Arrays.deepToString(nn.output_layer_weights));

        List<String[]> lines_test = Util.getLines(filePath_test);
        String[] header_test = lines_test.remove(0);
        String[] labels_test = Util.getLabels(lines_test);
        double[][] instances_test = Util.getData(lines_test);

        // scale the test according to our training data.
        rescaler.rescaleData(instances_test);

        // TODO: Compute and print the test accuracy
        int[] predictionsOnTestSet = nn.predict(instances_test);
        // TODO: Print accuracy achieved over this epoch
        double acc = Double.NaN, correctNum = 0.0;
        label_encoder = new LabelEncoder(labels_test);
        int[] desired_outputs = label_encoder.intEncode(labels_test);
        for (int i = 0; i < predictionsOnTestSet.length; i++) {
            int prediction = predictionsOnTestSet[i];
            if (prediction == desired_outputs[i]) {
                correctNum++;
            }
        }

        acc = (correctNum / instances_test.length) * 100;

        System.out.println(
                "After 100 Epochs on training,"
                           + "\n For the TEST set, we got  "
                           + correctNum + " correct predicted test instances"
                           + "\nThe size of test set is " + instances_test.length
                           + "\nwhich means, we got: " + correctNum + " out of "
                           + instances_test.length);

        System.out.println("acc = " + String.format("%.2f", acc) + " %");

        System.out.println("Finished!");
    }

}
