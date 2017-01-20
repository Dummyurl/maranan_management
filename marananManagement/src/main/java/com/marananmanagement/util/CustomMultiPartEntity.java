package com.marananmanagement.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

public class CustomMultiPartEntity extends MultipartEntity

{

	private final ProgressListener listener;
	private final Charset charset;

	public CustomMultiPartEntity(final ProgressListener listener,
			Charset charset) {
		super();
		this.listener = listener;
		this.charset = charset;
	}

	public CustomMultiPartEntity(final HttpMultipartMode mode,
			final ProgressListener listener, Charset charset) {
		super(mode);
		this.listener = listener;
		this.charset = charset;
	}

	public CustomMultiPartEntity(HttpMultipartMode mode, final String boundary,
			final Charset charset, final ProgressListener listener) {
		super(mode, boundary, charset);
		this.listener = listener;
		this.charset = charset;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.listener, this.charset));
	}

	public static interface ProgressListener {
		void transferred(long num);
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener listener;
		private long transferred;
		public final Charset charset;

		public CountingOutputStream(final OutputStream out,
				final ProgressListener listener, final Charset charset) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
			this.charset = charset;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			this.transferred += len;
			this.listener.transferred(this.transferred);
		}

		public void write(int b) throws IOException {
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred);
		}
	}
}