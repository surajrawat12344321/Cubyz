package cubyz.gui.text;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.Toolkit;

import org.joml.Vector2d;

import cubyz.gui.Component;
import cubyz.gui.Design;
import cubyz.rendering.Input;
import cubyz.rendering.Keys;
import cubyz.utils.json.*;

public class Text extends Component {
	//statics
	public static GraphicFont font = new GraphicFont();
	
	static {
		font.loadFromAwt();
		CubyzGraphics2D.instance.font = font;
	}

	private PrettyText prettyVersion;
	
	
	//state of the Text
	private boolean pressed;
	
	// Text and cursor/selection data.
	private String text = "";
	private TextLayout layout = null;
	private TextHitInfo cursorPosition = null;
	private TextHitInfo selectionStart = null;
	private boolean editable = false;
	
	
	@Override
	public String getID() {
		return "cubyz:text";
	}

	@Override
	public void create(JsonObject object, Component parent) {
		super.create(object, parent);
		setText(object.getString("text", ""));
		editable = object.getBool("editable", false);
	}
	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		
		if(!text.equals("")) {
			object.put("text", text);
		}
		object.put("editable", editable);
		return object;
	}

	/**
	 * Gets the x-position of the cursor on the text.
	 * @param cursorPosition
	 * @return
	 */
	private float getCursorX(TextHitInfo cursorPosition) {
		if(cursorPosition == null || layout == null) return 0;
		Point2D.Float cursorPos = new Point2D.Float();
		layout.hitToPoint(cursorPosition, cursorPos);
		return cursorPos.x*(float)height.getAsValue()/font.font.getSize();
	}

	/**
	 * Makes sure that the cursor is on the correct edge for further usage, by moving it one field and back.
	 * @param position `cursorPosition` or `selectionStart`
	 * @return
	 */
	private TextHitInfo fixEdge(TextHitInfo position) {
		if(position == null || layout == null) return position;
		if(text.length() == 0) return TextHitInfo.trailing(-1);
		if(layout.getNextLeftHit(position) != null) {
			position = layout.getNextLeftHit(position);
			position = layout.getNextRightHit(position);
		} else {
			position = layout.getNextRightHit(position);
			position = layout.getNextLeftHit(position);
		}
		return position;
	}

	public void addText(String string) {
		this.text += string;
		updateText();
	}
	public void setText(String string) {
		this.text = string;
		updateText();
	}
	
	/**
	 * Inserts a String at the current cursor position.
	 * @param string
	 */
	public void addTextAtCursor(String string) {
		if(cursorPosition != null) {
			if(selectionStart != null) deleteTextAtCursor(true); // overwrite selected text.
			int insertionIndex = cursorPosition.getCharIndex();
			if(!cursorPosition.isLeadingEdge()) insertionIndex++;
			this.text = text.substring(0, insertionIndex)+string+text.substring(insertionIndex);
			updateText();
			cursorPosition = TextHitInfo.leading(insertionIndex + string.length());
			
			cursorPosition = fixEdge(cursorPosition);
		}
	}

	/**
	 * Copies selected text to clipboard.
	 */
	public void copyText() {
		if(selectionStart == null) return; // Don't copy if nothing is selected.
		int[] selections = layout.getLogicalRangesForVisualSelection(cursorPosition, selectionStart);
		String result = "";
		for(int i = 0; i < selections.length; i += 2) {
			int start = selections[i];
			int end = selections[i+1];
			result += text.substring(start, end);
		}
		StringSelection selection = new StringSelection(result);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	/**
	 * Pastes text from clipboard at the current cursor position.
	 */
	public void pasteText() {
		// Check if there even is a String non-zero length on the clipboard:
		String pasted = "";
		try {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable clipBoardContent = clipboard.getContents(this);
			pasted = clipBoardContent.getTransferData(DataFlavor.stringFlavor).toString();
		} catch(Exception e) {
			return;
		}
		if(pasted.length() == 0) return;
		// Delete the current selection and replace it with the inserted value:
		if(selectionStart != null) {
			deleteTextAtCursor(true);
		}
		addTextAtCursor(pasted);
	}

	/**
	 * Copies selected text to clipboard and deletes it.
	 */
	public void cutText() {
		copyText();
		if(selectionStart != null)
			deleteTextAtCursor(true);
	}
	
	/**
	 * Removes the selected text or if no text is selected, removes the right or left character depending on what key is pressed.
	 * @param isRightDelete on which side the character should be removed.
	 */
	public void deleteTextAtCursor(boolean isRightDelete) {
		if(cursorPosition != null && layout != null) {
			boolean isLeading = cursorPosition.isLeadingEdge();
			int oldPositionIndex = cursorPosition.getCharIndex();
			// Make a selection to determine which character should be removed:
			if(selectionStart == null) { // If nothing is selected.
				if(isRightDelete) {
					selectionStart = layout.getNextRightHit(cursorPosition);
				} else {
					selectionStart = layout.getNextLeftHit(cursorPosition);
				}
				if(selectionStart == null) {
					return;
				}
			}
			int[] selection = layout.getLogicalRangesForVisualSelection(cursorPosition, selectionStart);
			selectionStart = null;
			// Remove all selected characters:
			for(int i = 0; i < selection.length; i += 2) {
				int start = selection[i];
				int end = selection[i+1];
				deleteTextRange(start, end);
				// Go through other indices and shift them:
				for(int j = i + 2; j < selection.length; j += 2) {
					if(selection[j] >= end) {
						selection[j] -= end - start;
						selection[j+1] -= end - start;
					}
				}
				// Also move the current cursor location:
				if(oldPositionIndex >= end || (oldPositionIndex == end-1 && !isLeading)) {
					oldPositionIndex -= end - start;
				}
			}
			updateText();
			// Update cursor:
			if(isLeading)
				cursorPosition = TextHitInfo.leading(oldPositionIndex);
			else
				cursorPosition = TextHitInfo.trailing(oldPositionIndex);
			
			cursorPosition = fixEdge(cursorPosition);
		}
	}
	private void deleteTextRange(int start, int end) {
		text = text.substring(0, start) + text.substring(end);
	}
	private void updateText() {
		if(Input.selectedText == this) {
			prettyVersion = null;
			width.setAsValue((float)layout.getBounds().getWidth()*height.getAsValue()/font.font.getSize());
		} else {
			prettyVersion = new PrettyText(text);
			width.setAsValue((float)prettyVersion.layout.getBounds().getWidth()*height.getAsValue()/font.font.getSize());
		}
		if(text.length() != 0) {
			layout = new TextLayout(text, font.font, font.fontGraphics.getFontRenderContext());
		} else {
			width.setAsValue(0);
			layout = null;
		}
	}
	/**
	 * Moves the cursor. Positive direction is to the right.
	 * @param offset
	 */
	public void moveCursor(int offset) {
		if(offset != 0) selectionStart = null;
		if(text.length() == 0) {
			cursorPosition = TextHitInfo.trailing(-1);
			return;
		}
		if(offset < 0) {
			while(offset++ < 0) {
				TextHitInfo newPosition = layout.getNextLeftHit(cursorPosition);
				if(newPosition != null) {
					cursorPosition = newPosition;
					break;
				}
			}
		} else if(offset > 0) {
			while(offset-- > 0) {
				TextHitInfo newPosition = layout.getNextRightHit(cursorPosition);
				if(newPosition != null) {
					cursorPosition = newPosition;
					break;
				}
			}
		}
		cursorPosition = fixEdge(cursorPosition);
	}

	public String getText() {
		return text;
	}
	
	@Override
	public void update(Design design,float parentalOffsetX,float parentalOffsetY) {
		super.update(design,parentalOffsetX,parentalOffsetY);
		if(!editable) return;
		Vector2d mousepos = Input.mousePosition;
		mousepos.x-= parentalOffsetX + left.getAsValue();
		mousepos.y-= parentalOffsetY + top.getAsValue();
		
		boolean hovered = (0<=mousepos.x&&
			0<=mousepos.y&&
			width.getAsValue()>=mousepos.x&&
			height.getAsValue()>=mousepos.y)
				&&design.hovered==null;
		if(hovered)
			design.hovered=this;
		
		boolean old_pressed = pressed;
		// The text can only be selected, when the mouse hovers the text field.
		pressed = hovered && Input.pressed(Keys.CUBYZ_GUI_PRESS_PRIMARY);
		if(pressed) {
			Input.selectedText = this;
			prettyVersion = null;
			width.setAsValue((float)layout.getBounds().getWidth()*height.getAsValue()/font.font.getSize());
		}
		if(pressed && !old_pressed) {
			float ratio = (float)height.getAsValue()/font.font.getSize();
			TextHitInfo info = layout.hitTestChar((float)mousepos.x/ratio, (float)mousepos.y/ratio);
			selectionStart = fixEdge(info);
		}
		if(pressed || old_pressed) {
			float ratio = (float)height.getAsValue()/font.font.getSize();
			TextHitInfo info = layout.hitTestChar((float)mousepos.x/ratio, (float)mousepos.y/ratio);
			cursorPosition = fixEdge(info);
			if(!pressed) {
				// Delete the selection if the cursorpositions are the same:
				if(cursorPosition.getCharIndex() == selectionStart.getCharIndex() && cursorPosition.isLeadingEdge() == selectionStart.isLeadingEdge()) {
					selectionStart = null;
				}
			}
		}
		if(prettyVersion == null && Input.selectedText != this) {
			prettyVersion = new PrettyText(text);
			selectionStart = null;
			cursorPosition = null;
			width.setAsValue((float)prettyVersion.layout.getBounds().getWidth()*height.getAsValue()/font.font.getSize());
		}
	}
	
	
	@Override
	public void draw(Design design,float parentalOffsetX,float parentalOffsetY) {
		CubyzGraphics2D.instance.textHeight = height.getAsValue();
		
		
		if(layout != null) {
			// Undo the ratio multiplication that is done later on the gpu:
			float ratio = (float)height.getAsValue()/font.font.getSize();
			if(prettyVersion == null) {
				layout.draw(CubyzGraphics2D.instance, (parentalOffsetX+left.getAsValue())/ratio, (parentalOffsetY+top.getAsValue()+height.getAsValue())/ratio);
			} else {
				prettyVersion.draw(ratio, CubyzGraphics2D.instance, (parentalOffsetX+left.getAsValue())/ratio, (parentalOffsetY+top.getAsValue()+height.getAsValue())/ratio);
			}
		}
		
		
		if(cursorPosition != null) {
			float cursorX = getCursorX(cursorPosition);
			CubyzGraphics2D.instance.setColor(0xff000000);
			CubyzGraphics2D.instance.drawLine(left.getAsValue() + parentalOffsetX + cursorX, top.getAsValue() + parentalOffsetY, 0, height.getAsValue());
			
			if(selectionStart != null) {
				float startX = left.getAsValue() + parentalOffsetX + cursorX;
				float selectionStartX = getCursorX(selectionStart);
				float endX = left.getAsValue() + parentalOffsetX + selectionStartX;
				CubyzGraphics2D.instance.setColor(0x7f000000);
				CubyzGraphics2D.instance.fillRect(Math.min(startX, endX), top.getAsValue() + parentalOffsetY, Math.abs(endX - startX), height.getAsValue());

			}
		}

		super.draw(design,parentalOffsetX,parentalOffsetY);
	}
}
