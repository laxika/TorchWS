package io.torch.exception;

public class NoSuchConstructorException extends ReflectiveOperationException {

    public NoSuchConstructorException(String str) {
        super(str);
    }
}
