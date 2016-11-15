package com.realcomp.prime.validation.file;

import com.realcomp.prime.validation.field.*;
import com.realcomp.prime.annotation.Validator;


@Validator("validateRecordCount")
public class RecordCountValidator extends LongRangeValidator{
}
