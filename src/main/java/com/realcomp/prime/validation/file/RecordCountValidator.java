package com.realcomp.prime.validation.file;

import com.realcomp.prime.validation.field.*;
import com.realcomp.prime.annotation.Validator;

/**
 *
 * @author krenfro
 */
@Validator("validateRecordCount")
public class RecordCountValidator extends LongRangeValidator{
}
