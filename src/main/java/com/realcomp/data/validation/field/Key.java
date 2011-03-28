
package com.realcomp.data.validation.field;

import com.realcomp.data.annotation.Validator;

/**
 * Marks the value as a 'key' and validates that the value is non-empty.
 * 
 * @author krenfro
 */
@Validator("key")
public class Key extends RequiredValidator {

}
