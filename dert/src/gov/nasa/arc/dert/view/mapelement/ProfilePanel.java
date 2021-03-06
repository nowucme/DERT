package gov.nasa.arc.dert.view.mapelement;

import gov.nasa.arc.dert.action.edit.CoordAction;
import gov.nasa.arc.dert.landscape.Landscape;
import gov.nasa.arc.dert.scene.MapElement;
import gov.nasa.arc.dert.scene.tool.Profile;
import gov.nasa.arc.dert.ui.ColorSelectionPanel;
import gov.nasa.arc.dert.ui.CoordTextField;
import gov.nasa.arc.dert.ui.DoubleTextField;
import gov.nasa.arc.dert.util.FileHelper;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.ardor3d.math.type.ReadOnlyVector3;

/**
 * Provides controls for setting options for profile tools.
 *
 */
public class ProfilePanel extends MapElementBasePanel {

	// Controls
	private ColorSelectionPanel colorList;
	private CoordTextField pALocation, pBLocation;
	private JLabel aElevLabel, bElevLabel;
	private JButton saveAsCSV, openButton;
	private DoubleTextField lineWidthText;

	// The profile
	private Profile profile;

	/**
	 * Constructor
	 * 
	 * @param parent
	 */
	public ProfilePanel(MapElementsPanel parent) {
		super(parent);
		icon = Profile.icon;
		type = "Profile";
		build(true, false, true);
	}

	@Override
	protected void build(boolean addNotes, boolean addLoc, boolean addCBs) {
		super.build(addNotes, addLoc, addCBs);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Location A"));
		pALocation = new CoordTextField(20, "location of end point A", Landscape.format, false) {
			@Override
			public void doChange(ReadOnlyVector3 coord) {
				if (!coord.equals(profile.getEndpointA())) {
					profile.setEndpointA(coord);
				}				
			}
		};
		CoordAction.listenerList.add(pALocation);
		panel.add(pALocation);
		contents.add(panel);

		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Elevation A"));
		aElevLabel = new JLabel("            ");
		panel.add(aElevLabel);
		contents.add(panel);

		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Location B"));
		pBLocation = new CoordTextField(20, "location of end point B", Landscape.format, false) {
			@Override
			public void doChange(ReadOnlyVector3 coord) {
				if (!coord.equals(profile.getEndpointB())) {
					profile.setEndpointB(coord);
				}				
			}
		};
		CoordAction.listenerList.add(pBLocation);
		panel.add(pBLocation);
		contents.add(panel);

		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Elevation B"));
		bElevLabel = new JLabel("            ");
		panel.add(bElevLabel);
		contents.add(panel);

		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Color"));
		colorList = new ColorSelectionPanel(Profile.defaultColor) {
			@Override
			public void doColor(Color color) {
				profile.setColor(color);
			}
		};
		panel.add(colorList);

		panel.add(new JLabel("Line Width", SwingConstants.RIGHT));
		lineWidthText = new DoubleTextField(8, Profile.defaultLineWidth, true, Landscape.format) {
			@Override
			protected void handleChange(double value) {
				if (Double.isNaN(value)) {
					return;
				}
				profile.setLineWidth((float) value);
			}
		};
		panel.add(lineWidthText);

		contents.add(panel);

		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		saveAsCSV = new JButton("Save As CSV");
		saveAsCSV.setToolTipText("save profile data formatted as comma separated values");
		saveAsCSV.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String fileName = FileHelper.getCSVFile();
				if (fileName == null) {
					return;
				}
				profile.saveAsCsv(fileName);
			}
		});
		saveAsCSV.setEnabled(false);
		panel.add(saveAsCSV);
		openButton = new JButton("Open Graph");
		openButton.setToolTipText("show the profile as a graph in a separate window");
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				profile.getState().getViewData().setVisible(true);
				profile.getState().open();
			}
		});
		openButton.setEnabled(false);
		panel.add(openButton);
		contents.add(panel);
	}

	@Override
	public void setMapElement(MapElement mapElement) {
		this.mapElement = mapElement;
		profile = (Profile) mapElement;
		setLocation(pALocation, aElevLabel, profile.getEndpointA());
		setLocation(pBLocation, bElevLabel, profile.getEndpointB());
		pinnedCheckBox.setSelected(profile.isPinned());
		lineWidthText.setValue(profile.getLineWidth());
		nameLabel.setText(profile.getName());
		colorList.setColor(profile.getColor());
		noteText.setText(profile.getState().getAnnotation());
		labelCheckBox.setSelected(profile.isLabelVisible());
		saveAsCSV.setEnabled(true);
		openButton.setEnabled(true);
	}

	@Override
	public void updateLocation(MapElement mapElement) {
		setLocation(pALocation, aElevLabel, profile.getEndpointA());
		setLocation(pBLocation, bElevLabel, profile.getEndpointB());
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (pALocation != null)
			CoordAction.listenerList.remove(pALocation);
		if (pBLocation != null)
			CoordAction.listenerList.remove(pBLocation);
	}

}
