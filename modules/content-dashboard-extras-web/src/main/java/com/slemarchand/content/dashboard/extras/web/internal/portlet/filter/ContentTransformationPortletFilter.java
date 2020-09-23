package com.slemarchand.content.dashboard.extras.web.internal.portlet.filter;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.io.WriterOutputStream;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnsyncPrintWriterPool;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.RenderResponseWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Sébastien Le Marchand
 *
 */
public abstract class ContentTransformationPortletFilter implements RenderFilter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
			RenderRequest renderRequest, RenderResponse renderResponse, FilterChain filterChain)
		throws IOException, PortletException {


		if (renderResponse instanceof LiferayPortletResponse) {
			_processLiferayPortletResponse(
				renderRequest, renderResponse, filterChain);

			return;
		}

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		RenderResponseWrapper renderResponseWrapper = new RenderResponseWrapper(
			renderResponse) {

			@Override
			public OutputStream getPortletOutputStream() throws IOException {
				if (_calledGetWriter) {
					throw new IllegalStateException(
						"Unable to obtain OutputStream because Writer is " +
							"already in use");
				}

				if (_outputStream != null) {
					return _outputStream;
				}

				_outputStream = new WriterOutputStream(unsyncStringWriter);

				_calledGetOutputStream = true;

				return _outputStream;
			}

			@Override
			public PrintWriter getWriter() throws IOException {
				if (_calledGetOutputStream) {
					throw new IllegalStateException(
						"Unable to obtain Writer because OutputStream is " +
							"already in use");
				}

				if (_printWriter != null) {
					return _printWriter;
				}

				_printWriter = UnsyncPrintWriterPool.borrow(unsyncStringWriter);

				_calledGetWriter = true;

				return _printWriter;
			}

			private boolean _calledGetOutputStream;
			private boolean _calledGetWriter;
			private OutputStream _outputStream;
			private PrintWriter _printWriter;

		};

		filterChain.doFilter(renderRequest, renderResponseWrapper);

		PrintWriter printWriter = renderResponse.getWriter();

		try {
			printWriter.write(
				transform(
					renderRequest, unsyncStringWriter.toString()));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws PortletException {
	}
	
	abstract protected String transform(RenderRequest renderRequest, String content) throws Exception;

	abstract protected Portal getPortal();

	private void _processLiferayPortletResponse(
			RenderRequest renderRequest, RenderResponse renderResponse,
			FilterChain filterChain)
		throws IOException, PortletException {

		filterChain.doFilter(renderRequest, renderResponse);

		HttpServletResponse httpServletResponse =
			getPortal().getHttpServletResponse(renderResponse);

		BufferCacheServletResponse bufferCacheServletResponse =
			(BufferCacheServletResponse)httpServletResponse;

		try {
			ServletResponseUtil.write(
				httpServletResponse,
				transform(
					renderRequest,
					bufferCacheServletResponse.getString()));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

}