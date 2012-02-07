package org.onexus.ui;

public interface IResourceActivator {
    
    public void bind(IResourceRegister resourceRegister);
    
    public void unbind(IResourceRegister resourceRegister);

}
