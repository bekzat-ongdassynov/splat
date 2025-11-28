package splat.executor;
// wrapper for str runtime vals
public class StringValue extends Value {

	private String value;
	
	public StringValue(String value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
}