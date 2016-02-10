package nodes;

import nodes.singals.Signal;

/**
 * This enum is used to specify the return stati of the code that is executed by
 * a {@link Node} can be.
 * 
 * <ul>
 * <li>SUCCESS - The code terminated successfully and the {@link Signal} should
 * be passed on.</li>
 * <li>EXCEPTION - The code terminated with an exception. The exception output
 * will recieve the {@link Signal}.</li>
 * <li>NOSIGNAL - The code terminated and the {@link Signal} should NOT be
 * passed any further.</li>
 * </ul>
 * 
 * @author Roland Wallner
 *
 */
public enum ReturnCode {
	SUCCESS, EXCEPTION, NOSIGNAL
}
