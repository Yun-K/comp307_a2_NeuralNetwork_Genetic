package part1;

import java.util.Arrays;

public class NeuralNetwork {
    public final double[][] hidden_layer_weights;

    public final double[][] output_layer_weights;

    private final int num_inputs;

    private final int num_hidden;

    private final int num_outputs;

    private final double learning_rate;

    public NeuralNetwork(int num_inputs, int num_hidden, int num_outputs,
            double[][] initial_hidden_layer_weights, double[][] initial_output_layer_weights,
            double learning_rate) {
        // Initialise the network
        this.num_inputs = num_inputs;
        this.num_hidden = num_hidden;
        this.num_outputs = num_outputs;

        this.hidden_layer_weights = initial_hidden_layer_weights;
        this.output_layer_weights = initial_output_layer_weights;

        this.learning_rate = learning_rate;
    }

    /**
     * Description: <br/>
     * Calculate neuron activation for an input.
     * <p>
     * It maps the input ( x axis ) to values between 0 and 1.
     * 
     * @author Yun Zhou
     * @param input
     *            should be the out put value of the forward_pass()
     * @return
     */
    public double sigmoid(double input) {
        double output = Double.NaN; // TODO!

        // my codes start here:

        // the formular is from lec07
        double numerator = 1;
        double denominator = 1 + Math.exp(-input);

        output = numerator / denominator;

        return output;
    }

    /**
     * Description: <br/>
     * Feed forward pass input to a network output.
     * <p>
     * Each instance got 4 features, so the input arg is an array of 4 features.
     * </p>
     *
     * @author Yun Zhou
     * @param inputs
     *            each instance has an array of 4 features, which are: bill_length_mm,
     *            bill_depth_mm, flipper_length_mm, body_mass_g
     * @return the 2d array which holds the output values for each layer of their nodes. The
     *         1st row is for nodes in hidden layer, 2nd row is for nodes in output layer
     * 
     */
    public double[][] forward_pass(double[] inputs) {
        // for hidden layer
        double[] hidden_layer_outputs = new double[num_hidden];
        for (int i = 0; i < num_hidden; i++) {
            // TODO! Calculate the weighted sum, and then compute the final output.

            // the formular is from the lec-08,
            double output = 0;
            int inputNodeIndex = -1;
            // loop through {(w15,w16),(w25,W26),(W35,W36),(w45,w46)}
            for (double[] input_node_weights : this.hidden_layer_weights) {
                // calculate the weighted sum,
                // it's i since either w15/w25/w35/w45 OR w16/w26/w36/w46, either i==0 or i==1
                double weighted = input_node_weights[i];
                // times the value of each input node, which is I1,I2,I3,I4
                output += weighted * inputs[++inputNodeIndex];
            }

            output = sigmoid(output);

            hidden_layer_outputs[i] = output;
        }

        // for output layer,the calculation is the same as above
        double[] output_layer_outputs = new double[num_outputs];
        for (int i = 0; i < num_outputs; i++) {
            // TODO! Calculate the weighted sum, and then compute the final output.

            double output = 0;
            int hiddenNodeIndex = -1;
            for (double[] hidden_node_weights : this.output_layer_weights) {
                // calculate the weighted sum first
                double weighted = hidden_node_weights[i];
                // times the value of the hidden nodes
                output += weighted * hidden_layer_outputs[++hiddenNodeIndex];
            }

            output = sigmoid(output);// pass it into sigmoid
            output_layer_outputs[i] = output;
        }
        return new double[][] { hidden_layer_outputs, output_layer_outputs };
    }

    public double[][][] backward_propagate_error(double[] inputs, double[] hidden_layer_outputs,
            double[] output_layer_outputs, int desired_outputs) {

        double[] output_layer_betas = new double[num_outputs];
        // TODO! Calculate output layer betas.

        /*
         * first, assign the desired output, so that can calculate the error
         */
        // encode 1 as [1, 0, 0], 2 as [0, 1, 0],
        // and 3 as [0, 0, 1] (to fit with our network outputs!)
        int[] desiredOutputs_array = new int[3];
        switch (desired_outputs) {
        case 0:
            desiredOutputs_array = new int[] { 1, 0, 0 };
            break;
        case 1:
            desiredOutputs_array = new int[] { 0, 1, 0 };
            break;
        case 2:
            desiredOutputs_array = new int[] { 0, 0, 1 };
            break;
        }

        /*
         * from lec08,slide 14-15
         */
        // for calculating the output layer
        // the error:beta = desired-real
        for (int i = 0; i < output_layer_outputs.length; i++) {
            double outputLayerNode_output = output_layer_outputs[i];
            // beta = desired - real output
            double beta = desiredOutputs_array[i] - outputLayerNode_output;
            // assign the corresponding beta into array
            output_layer_betas[i] = beta;
        }
        System.out.println("OL betas: " + Arrays.toString(output_layer_betas));

        /*
         * for hidden layers error: beta = weight1 * outPutNodeError(aka beta)1 * slope1, like
         * the forward slope = (1-Output)*outout
         */
        double[] hidden_layer_betas = new double[num_hidden];
        // TODO! Calculate hidden layer betas.

        for (int i = 0; i < hidden_layer_outputs.length; i++) {
            double beta = 0;
            for (int j = 0; j < output_layer_outputs.length; j++) {
                double outputLayerNode_output = output_layer_outputs[j];// assign corresponding
                                                                        // temp var
                double slope = outputLayerNode_output * (1 - outputLayerNode_output);
                // weight * value
                beta += output_layer_weights[i][j]
                        * slope // slope=Oj*(1-Oj)
                        * output_layer_betas[j];// beta (aka error)
            }

            hidden_layer_betas[i] = beta;

        }

        System.out.println("HL betas: " + Arrays.toString(hidden_layer_betas));

        // TODO! Calculate output layer weight changes.
        /*
         * lec08, slide 16, formula:delta Wij = LearningRate*Oi*(Oj*(1-Oj))*Betaj
         * 
         * Now the (beta)error signal for each neuron is computed, start to compute the
         * derivative (differential coefficient) of each neuron,
         * 
         * and the weights coefficients of each neuron input node may be modified
         */
        // This is a HxO array (H hidden nodes, O outputs)
        double[][] delta_output_layer_weights = new double[num_hidden][num_outputs];

        for (int i = 0; i < hidden_layer_outputs.length; i++) {
            for (int j = 0; j < output_layer_outputs.length; j++) {
                // get slope first
                double slope = output_layer_outputs[j] * (1 - output_layer_outputs[j]);
                // then do the calculation
                double weightChange = this.learning_rate
                        * hidden_layer_outputs[i]// Oi
                        * slope// slope = Oj*(1-Oj)
                        * output_layer_betas[j];

                delta_output_layer_weights[i][j] = weightChange;
            }

        }

        /*
         * the same calculation as above delta_output_layer_weights.
         * 
         */
        // This is a IxH array (I inputs, H hidden nodes)
        double[][] delta_hidden_layer_weights = new double[num_inputs][num_hidden];
        // TODO! Calculate hidden layer weight changes.
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < hidden_layer_outputs.length; j++) {
                // get slope first
                double slope = hidden_layer_outputs[j] * (1 - hidden_layer_outputs[j]);
                // then do the calculation
                double weightChange = this.learning_rate
                        * inputs[i]// Oi
                        * slope// slope = Oj*(1-Oj)
                        * hidden_layer_betas[j];

                delta_hidden_layer_weights[i][j] = weightChange;
            }
        }

        // Return the weights we calculated, so they can be used to update all the weights.
        return new double[][][] { delta_output_layer_weights, delta_hidden_layer_weights };
    }

    public void update_weights(double[][] delta_output_layer_weights,
            double[][] delta_hidden_layer_weights) {
        // TODO! Update the weights
        System.out.println("Placeholder");
    }

    public void train(double[][] instances, int[] desired_outputs, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            System.out.println("epoch = " + epoch);
            int[] predictions = new int[instances.length];
            for (int i = 0; i < instances.length; i++) {
                double[] instance = instances[i];
                double[][] outputs = forward_pass(instance);
                double[][][] delta_weights = backward_propagate_error(instance, outputs[0],
                        outputs[1], desired_outputs[i]);
                int predicted_class = -1; // TODO!
                predictions[i] = predicted_class;

                // We use online learning, i.e. update the weights after every instance.
                update_weights(delta_weights[0], delta_weights[1]);
            }

            // Print new weights
            System.out
                    .println("Hidden layer weights \n" + Arrays.deepToString(hidden_layer_weights));
            System.out.println(
                    "Output layer weights  \n" + Arrays.deepToString(output_layer_weights));

            // TODO: Print accuracy achieved over this epoch
            double acc = Double.NaN;
            System.out.println("acc = " + acc);
        }
    }

    public int[] predict(double[][] instances) {
        int[] predictions = new int[instances.length];
        for (int i = 0; i < instances.length; i++) {
            double[] instance = instances[i];
            double[][] outputs = forward_pass(instance);

            int predicted_class = -1; // TODO !Should be 0, 1, or 2.

            /*
             * my codes start here:
             * 
             */
            // the 2nd row of the outputs(i.e. forward_pass()) is the output layer,
            // so assign it
            double[] output_layer_outputs = outputs[1];
            for (int index = 0; index < output_layer_outputs.length; index++) {
                predicted_class = (int) output_layer_outputs[index];
            }

            // my codes end
            predictions[i] = predicted_class;
        }
        return predictions;
    }

}
