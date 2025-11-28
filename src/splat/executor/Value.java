package splat.executor;
// base class for all runtime values
public abstract class Value {
	// subclasses implement this to return their value
	public abstract Object getValue();
	// helper to get as int
	public int getIntValue() {
		return (int)getValue();
	}
	// helper to get as bool
	public boolean getBoolValue() {
		return (boolean)getValue();
	}
	// helper to get as str
	public String getStringValue() {
		return (String)getValue();
	}
}