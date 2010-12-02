package uk.ac.kcl.informatics.sourcesource;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import uk.ac.kcl.informatics.opmbuild.Account;
import uk.ac.kcl.informatics.opmbuild.Artifact;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.Process;
import uk.ac.kcl.informatics.opmbuild.Used;
import uk.ac.kcl.informatics.opmbuild.WasDerivedFrom;
import uk.ac.kcl.informatics.opmbuild.WasGeneratedBy;

public class Recorder {
    public static final Recorder _recorder = new Recorder ();
    /** Maps statements to the number of times that statement was executed */
    private Map<String, Integer> _pointIteration;
    /** Maps statements to the process last occurrence of that class */
    private Map<String, Process> _processPoints;
    /** Maps variable names to the artifacts for their last occurrence */
    private Map<String, Artifact> _mostRecent;
    /** Maps occurrence classes to lists of plug-ins called for each class */
    private Map<String, List<PlugIn>> _plugInPoints;
    /** Maps method invocations to index of arguments passed */
    private Map<String, Map<Integer, String>> _argumentPasses;
    /** Stack of accounts corresponding to call stack, with current invocation's account on top */
    private Stack<Account> _accountStack;
    /** The fine-grained account of the method just invoked, if any */
    private Account _subProcessAccount;
    /** The artifact just returned from a method invocation, if any */
    private Artifact _returned;
    /** The unique ID for this execution */
    private String _execution;
    /** The graph being populated during recording */
    private Graph _graph;

    private Recorder () {
        _pointIteration = new HashMap<String, Integer> ();
        _processPoints = new HashMap<String, Process> ();
        _mostRecent = new HashMap<String, Artifact> ();
        _plugInPoints = new HashMap<String, List<PlugIn>> ();
        _argumentPasses = new HashMap<String, Map<Integer, String>> ();
        _accountStack = new Stack<Account> ();
        _subProcessAccount = null;
        _graph = new Graph ();
        _returned = null;
        _execution = String.valueOf (System.currentTimeMillis ());
        pushLocal ();
    }

    private Account currentAccount () {
        return _accountStack.peek ();
    }

    private Map<Integer, String> getArgumentPasses (String methodID) {
        Map<Integer, String> passes = _argumentPasses.get (methodID);

        if (passes == null) {
            passes = new HashMap<Integer, String> ();
            _argumentPasses.put (methodID, passes);
        }

        return passes;
    }

    /**
     * @return The unique ID for this execution
     */
    public static String getExecutionID () {
        return _recorder._execution;
    }

    /**
     * @return The graph populated in recording
     */
    public static Graph getGraph () {
        return _recorder._graph;
    }

    /**
     * Returns the number of iterations that have been made a given statement
     * in this execution.
     * @param statement The ID of the statement
     * @return The number of iterations
     */
    private int getNumberOfIterations (String statement) {
        Integer iteration = _pointIteration.get (statement);

        if (iteration == null) {
            iteration = 0;
            _pointIteration.put (statement, iteration);
        }

        return iteration;
    }

    /**
     * Returns the most recent value (as an Artifact) of a given variable.
     * @param variableName
     * @return
     */
    public static Artifact getMostRecentValue (String variableName) {
        return _recorder.getMostRecentValueLocal (variableName);
    }

    /**
     * Returns the most recent value (as an Artifact) of a given variable.
     * @param variableName
     * @return
     */
    private Artifact getMostRecentValueLocal (String variableName) {
        return _mostRecent.get (variableName);
    }

    /**
     * @param statment The ID of a statement
     * @return The PlugIns associated with that statement
     */
    public List<PlugIn> getPlugIns (String statment) {
        List<PlugIn> plugins = _plugInPoints.get (statment);

        if (plugins == null) {
            plugins = new LinkedList<PlugIn> ();
            _plugInPoints.put (statment, plugins);
        }

        return plugins;
    }

    /**
     * Associates a PlugIn with a given statement, to be called following that
     * statement's execution.
     * @param statement The ID of a statement
     * @param plugin The PlugIn to associate with that statement
     */
    public static void insertPlugIn (String statement, PlugIn plugin) {
        _recorder.insertPlugInLocal (statement, plugin);
    }

    /**
     * Associates a PlugIn with a given statement, to be called following that
     * statement's execution.
     * @param statement The ID of a statement
     * @param plugin The PlugIn to associate with that statement
     */
    public void insertPlugInLocal (String occurrenceClass, PlugIn plugin) {
        getPlugIns (occurrenceClass).add (plugin);
    }

    /**
     * Record that a statement has been executed.
     */
    public static void process (String statement) {
        _recorder.processLocal (statement);
    }

    /**
     * Record that a statement has been executed.
     */
    private void processLocal (String point) {
        int instance = getNumberOfIterations (point) + 1;
        Process process = new Process ();
        Account coarseAccount;

        process.annotate (SourceSourceConstants._point, point);
        process.annotate (SourceSourceConstants._iteration, String.valueOf (instance));
        process.annotate (SourceSourceConstants._execution, _execution);
        currentAccount ().add (process);

        _pointIteration.put (point, instance);
        _processPoints.put (point, process);

        if (_subProcessAccount != null) {
            coarseAccount = new Account ();
            coarseAccount.add (process);
            _subProcessAccount = null;
        }

        for (PlugIn plugin : getPlugIns (point)) {
            plugin.process (_graph, process);
        }
    }

    /**
     * Record that an argument was received as a parameter within a method invocation
     * @param methodID Name of the method
     * @param invocationID ID of the invoking statement
     * @param index Index of this argument in the parameter list
     * @param variable The name of the local parameter
     */
    public static void received (String methodID, String invocationID, int index, String variable) {
        _recorder.receivedLocal (methodID, invocationID, index, variable);
    }

    /**
     * Record that an argument was received as a parameter within a method invocation
     * @param methodID Name of the method
     * @param invocationID ID of the invoking statement
     * @param index Index of this argument in the parameter list
     * @param variable The name of the local parameter
     */
    private void receivedLocal (String methodID, String invocationID, int index, String variable) {
        String passed = getArgumentPasses (methodID).get (index);

        if (_accountStack.size () > 1) {
            Account subaccount = _accountStack.pop ();
            //used (invocationID, passed, SourceSourceConstants._passedAsArgument);
            derived (variable, passed);
            _accountStack.push (subaccount);
        }
        generated (variable, SourceSourceConstants._receivedArgument, invocationID);
    }

    /**
     * Records that the given variable is about to be returned
     * @param variable The name of the variable whose value is being returned
     */
    public static void returned (String variable) {
        _recorder.returnedLocal (variable);
    }

    /**
     * Records that the given variable is about to be returned
     * @param variable The name of the variable whose value is being returned
     */
    public void returnedLocal (String variable) {
        _returned = _mostRecent.get (variable);
    }

    public static void pass (String methodID, int index, String variable) {
        _recorder.passLocal (methodID, index, variable);
    }

    private void passLocal (String methodID, int index, String variable) {
        getArgumentPasses (methodID).put (index, variable);
    }

    public static void push () {
        _recorder.pushLocal ();
    }

    private void pushLocal () {
        Account account = new Account ();

        _graph.add (account);
        _returned = null;
        _accountStack.push (account);
    }

    public static void pop () {
        _recorder.popLocal ();
    }

    private void popLocal () {
        _subProcessAccount = _accountStack.pop ();
        if (_subProcessAccount.isEmpty ()) {
            _subProcessAccount = null;
        }
    }

    public static void used (String effectStatement, String causeVariable, String causeRole) {
        _recorder.usedLocal (effectStatement, causeVariable, causeRole);
    }

    private void usedLocal (String effectStatement, String causeVariable, String causeRole) {
        Artifact cause = _mostRecent.get (causeVariable);
        Process effect = _processPoints.get (effectStatement);

        if (cause != null && effect != null) {
            currentAccount ().add (new Used (effect, causeRole, cause));
        }

        for (PlugIn plugin : getPlugIns (effectStatement)) {
            plugin.used (getGraph (), effect, cause, causeRole);
        }
    }

    public static void variable (String name, String point, Object value) {
        _recorder.variableLocal (name, point, value);
    }

    public void variableLocal (String persistent, String point, Object value) {
        int instance = getNumberOfIterations (point);
        Artifact artifact = new Artifact ();

        artifact.annotate (SourceSourceConstants._point, point);
        artifact.annotate (SourceSourceConstants._iteration, String.valueOf (instance));
        artifact.annotate (SourceSourceConstants._variable, persistent);
        artifact.annotate (SourceSourceConstants._variableValue, value.toString ());
        artifact.annotate (SourceSourceConstants._execution, _execution);
        currentAccount ().add (artifact);

        _mostRecent.put (persistent, artifact);

        for (PlugIn plugin : getPlugIns (point)) {
            plugin.variable (getGraph (), artifact);
        }
    }

    public static void derived (String effectVariable, String causeVariable) {
        _recorder.derivedLocal (effectVariable, causeVariable);
    }

    private void derivedLocal (String effectVariable, String causeVariable) {
        Artifact effect = _mostRecent.get (effectVariable);
        Artifact cause = _mostRecent.get (causeVariable);

        if (cause != null && effect != null) {
            currentAccount ().add (new WasDerivedFrom (effect, cause));
        }
    }

    public static void generated (String effectPersistent, String effectRole, String causeClass) {
        _recorder.generatedLocal (effectPersistent, effectRole, causeClass);
    }

    private void generatedLocal (String effectPersistent, String effectRole, String causeClass) {   // boolean onlyAsDefault
        Artifact effect = _mostRecent.get (effectPersistent);
        Process cause = _processPoints.get (causeClass);

        if (cause != null && effect != null) {
            currentAccount ().add (new WasGeneratedBy (effect, effectRole, cause));
        }
        if (_returned != null) {
            currentAccount ().add (new WasDerivedFrom (effect, _returned));
            _returned = null;
        }

        for (PlugIn plugin : getPlugIns (causeClass)) {
            plugin.wasGeneratedBy (getGraph (), effect, effectRole, cause);
        }
    }
}
