package org.worldsproject.puzzle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class PuzzleView extends View implements OnGestureListener,
		OnDoubleTapListener
{
	private static final String DEBUG = "PuzzleView";
	private Resources r;
	private Puzzle puzzle;
	private GestureDetector gesture;
	private Piece tapped;
	private boolean firstDraw = true;

	public PuzzleView(Context context)
	{
		super(context);
		r = context.getResources();

		loadPuzzle();
	}

	public PuzzleView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		r = context.getResources();

		loadPuzzle();
	}

	public PuzzleView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		r = context.getResources();

		loadPuzzle();
	}

	private void loadPuzzle()
	{
		gesture = new GestureDetector(this.getContext(), this);

		Bitmap[] monsters = {
				BitmapFactory.decodeResource(r, R.drawable.monster1),
				BitmapFactory.decodeResource(r, R.drawable.monster2),
				BitmapFactory.decodeResource(r, R.drawable.monster3),
				BitmapFactory.decodeResource(r, R.drawable.monster4),
				BitmapFactory.decodeResource(r, R.drawable.monster5),
				BitmapFactory.decodeResource(r, R.drawable.monster6),
				BitmapFactory.decodeResource(r, R.drawable.monster7),
				BitmapFactory.decodeResource(r, R.drawable.monster8),
				BitmapFactory.decodeResource(r, R.drawable.monster9),
				BitmapFactory.decodeResource(r, R.drawable.monster10),
				BitmapFactory.decodeResource(r, R.drawable.monster11),
				BitmapFactory.decodeResource(r, R.drawable.monster12), };

		puzzle = new Puzzle(monsters, 4);
	}

	@Override
	public void onDraw(Canvas canvas)
	{

		if (firstDraw)
		{
			firstDraw = false;
			puzzle.shuffle(this.getWidth(), this.getHeight());
		}
		puzzle.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return gesture.onTouchEvent(event);
	}

	@Override
	public boolean onDoubleTap(MotionEvent arg0)
	{
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e)
	{

		for (Piece p : this.puzzle.getPieces())
		{
			if (p.inMe((int) e.getX(), (int) e.getY()))
			{
				Log.v(DEBUG, "Turn");
				p.turn();
			}
			this.invalidate();
			break;
		}
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e)
	{
		if (checkSurroundings(tapped))
			this.invalidate();

		return true;
	}

	@Override
	public boolean onDown(MotionEvent e1)
	{
		// Get the piece that is under this tap.
		Piece possibleNewTapped = null;
		boolean nothing = true;

		for (Piece p : this.puzzle.getPieces())
		{
			if (p.inMe((int) e1.getX(), (int) e1.getY()))
			{
				if (p == tapped)
				{
					possibleNewTapped = null;
					break;
				}
				else
				{
					possibleNewTapped = p;
				}
				
				nothing = false;
			}
		}

		if (possibleNewTapped != null)
		{
			tapped = possibleNewTapped;
		}
		
		if(!nothing)
		{
			tapped = null;
		}

		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY)
	{
		if (checkSurroundings(tapped))
			this.invalidate();

		return true;
	}

	@Override
	public void onLongPress(MotionEvent e)
	{
		Log.v(DEBUG, "Long Press recorded");

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY)
	{
		if (tapped == null) // We aren't hitting a piece
		{
			for (Piece p : this.puzzle.getPieces())
			{
				p.setX(p.getX() - (int)distanceX);
				p.setY(p.getY() - (int)distanceY);
			}
			
			this.invalidate();
		}
		else
		{
			tapped.getGroup().translate((int) distanceX, (int) distanceY);

			this.invalidate();
		}
		return true;
	}

	private boolean checkSurroundings(Piece tapped)
	{
		if (tapped == null || tapped.getOrientation() != 0)
		{
			return false;
		}

		boolean rv = false;

		if (tapped.inLeft())
		{
			tapped.snap(tapped.getLeft());
			rv = true;
		}

		if (tapped.inRight())
		{
			tapped.snap(tapped.getRight());
			rv = true;
		}

		if (tapped.inBottom())
		{
			tapped.snap(tapped.getBottom());
			rv = true;
		}

		if (tapped.inTop())
		{
			tapped.snap(tapped.getTop());
			rv = true;
		}

		return rv;
	}

	@Override
	public void onShowPress(MotionEvent e)
	{
		Log.v(DEBUG, "Show Press recorded");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		if (checkSurroundings(tapped))
			this.invalidate();

		return true;
	}

	public void zoomIn()
	{
		this.puzzle.zoomIn();
		this.invalidate();
	}

	public void zoomOut()
	{
		this.puzzle.zoomOut();
		this.invalidate();
	}
}
