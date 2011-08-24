package org.arc4eclipse.debugMessagePanel.views;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseLogger;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.repositoryFacardConstants.RepositoryFacardConstants;
import org.arc4eclipse.swtBasics.SwtBasicConstants;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.images.ImageButtons;
import org.arc4eclipse.swtBasics.text.ConfigForTitleAnd;
import org.arc4eclipse.swtBasics.text.TitleAndStyledTextField;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class DebugMessagePanel extends Composite implements IArc4EclipseLogger {
	private final int logSize = 8;
	private final List<String> log = Collections.synchronizedList(Lists.<String> newList());
	private final TitleAndStyledTextField titleAndStyledTextField;

	public DebugMessagePanel(Composite parent, int style, ConfigForTitleAnd config, IArc4EclipseRepository repository) {
		super(parent, style);
		setLayout(new FormLayout());

		titleAndStyledTextField = new TitleAndStyledTextField(config, this, "Messages");
		FormData fd_titleAndStyledTextField = new FormData();
		fd_titleAndStyledTextField.bottom = new FormAttachment(100, 0);
		fd_titleAndStyledTextField.right = new FormAttachment(100, 0);
		fd_titleAndStyledTextField.top = new FormAttachment(0, 0);
		fd_titleAndStyledTextField.left = new FormAttachment(0, 0);
		titleAndStyledTextField.setLayoutData(fd_titleAndStyledTextField);
		repository.addLogger(this);
		ImageButtons.addRowButton(titleAndStyledTextField, SwtBasicConstants.clearKey, DebugPanelConstants.clearKey, new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				log.clear();
				titleAndStyledTextField.setText("");
			}
		});
	}

	@Override
	public void sendingRequest(String method, String url, Map<String, Object> parameters, Map<String, Object> context) {
		log("> {0} {1}\nParameters: {2}\nContext: {3}", method, url, parameters, context);
	}

	@Override
	public void receivedReply(IResponse response, Object data, Map<String, Object> context) {
		int statusCode = response.statusCode();
		String responseString = RepositoryFacardConstants.okStatusCodes.contains(statusCode) ? "<Ok>" : response.asString();
		log("< {0} {1}\n{2}\nData: {3}\nContext: {4}", statusCode, response.url(), responseString, data, context);
	}

	private void log(final String pattern, final Object... arguments) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				String message = MessageFormat.format(pattern, arguments);
				String text = Strings.addToRollingLog(log, logSize, "\n", message);
				titleAndStyledTextField.setText(text);
			}
		});
	}

}
