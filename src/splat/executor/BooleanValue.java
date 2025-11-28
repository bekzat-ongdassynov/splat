package splat.executor;
// wrapper for bool runtime vals
public class BooleanValue extends Value {

	private boolean value;
	
	public BooleanValue(boolean value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
}