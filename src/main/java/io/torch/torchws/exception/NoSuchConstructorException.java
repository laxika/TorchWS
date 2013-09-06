package io.torch.torchws.exception;

public class NoSuchConstructorException extends ReflectiveOperationException {

    public NoSuchConstructorException(String str) {
        super(str);
    }
}
