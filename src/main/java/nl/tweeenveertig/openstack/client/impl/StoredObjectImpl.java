package nl.tweeenveertig.openstack.client.impl;

import nl.tweeenveertig.openstack.client.Container;
import nl.tweeenveertig.openstack.headers.Header;
import nl.tweeenveertig.openstack.headers.object.ObjectContentType;
import nl.tweeenveertig.openstack.model.DownloadInstructions;
import nl.tweeenveertig.openstack.model.UploadInstructions;
import nl.tweeenveertig.openstack.client.StoredObject;
import nl.tweeenveertig.openstack.client.core.AbstractStoredObject;
import nl.tweeenveertig.openstack.command.identity.access.Access;
import nl.tweeenveertig.openstack.command.object.*;
import org.apache.http.client.HttpClient;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StoredObjectImpl extends AbstractStoredObject {

    public StoredObjectImpl(Container container, String name) {
        super(container, name);
    }

    public InputStream downloadObjectAsInputStream() {
        return downloadObjectAsInputStream(new DownloadInstructions());
    }

    public InputStream downloadObjectAsInputStream(DownloadInstructions downloadInstructions) {
        return new DownloadObjectAsInputStreamCommand(getAccount(), getClient(), getAccess(), getContainer(), this, downloadInstructions).call();
    }

    public byte[] downloadObject() {
        return downloadObject(new DownloadInstructions());
    }

    public byte[] downloadObject(DownloadInstructions downloadInstructions) {
        return new DownloadObjectAsByteArrayCommand(getAccount(), getClient(), getAccess(), getContainer(), this, downloadInstructions).call();
    }

    public void downloadObject(File targetFile) {
        downloadObject(targetFile, new DownloadInstructions());
    }

    public void downloadObject(File targetFile, DownloadInstructions downloadInstructions) {
        new DownloadObjectToFileCommand(getAccount(), getClient(), getAccess(), getContainer(), this, downloadInstructions, targetFile).call();
    }

    public void uploadObject(UploadInstructions uploadInstructions) {
        new UploadObjectCommand(getAccount(), getClient(), getAccess(), getContainer(), this, uploadInstructions).call();
    }

    public void uploadObject(InputStream inputStream) {
        uploadObject(new UploadInstructions(inputStream));
    }

    public void uploadObject(byte[] fileToUpload) {
        uploadObject(new UploadInstructions(fileToUpload));
    }

    public void uploadObject(File fileToUpload) {
        uploadObject(new UploadInstructions(fileToUpload));
    }

    public void delete() {
        new DeleteObjectCommand(getAccount(), getClient(), getAccess(), getContainer(), this).call();
    }

    public void copyObject(Container targetContainer, StoredObject targetObject) {
        new CopyObjectCommand(getAccount(), getClient(), getAccess(), getContainer(), this, targetContainer, targetObject).call();
    }

    public void setContentType(String contentType) {
        checkForInfo();
        new ObjectMetadataCommand(
                getAccount(), getClient(), getAccess(), getContainer(),
                this, info.getHeadersIncludingContentType(contentType)).call();
    }

    public String getPublicURL() {
        return getAccess().getPublicURL() + "/" + getContainer().getName() + "/" + getName();
    }

    protected AccountImpl getAccount() {
        return (AccountImpl)getContainer().getAccount();
    }

    @Override
    public ContainerImpl getContainer() {
        return (ContainerImpl)super.getContainer();
    }

    protected HttpClient getClient() {
        return getContainer().getClient();
    }

    protected Access getAccess() {
        return getContainer().getAccess();
    }

    @Override
    protected void saveMetadata() {
        new ObjectMetadataCommand(getAccount(), getClient(), getAccess(), getContainer(), this, info.getMetadata()).call();
    }

    protected void getInfo() {
        this.info = new ObjectInformationCommand(getAccount(), getClient(), getAccess(), getContainer(), this).call();
        this.setInfoRetrieved();
    }

}
