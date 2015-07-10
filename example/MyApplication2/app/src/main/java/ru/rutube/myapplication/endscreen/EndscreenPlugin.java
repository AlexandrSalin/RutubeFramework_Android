package ru.rutube.myapplication.endscreen;

import android.content.Context;
import android.view.View;

import ru.rutube.RutubePlayerListenerHelper;
import ru.rutube.interfaces.IRutubePlayer;
import ru.rutube.interfaces.IRutubePlayerListener;
import ru.rutube.interfaces.IRutubePlayerPlugin;
import ru.rutube.models.RutubePlayerOperation;
import ru.rutube.models.RutubePlayerState;
import ru.rutube.myapplication.endscreen.ui.EndscreenView;

/**
 * Created by Juraldinio on 7/8/15.
 */
public class EndscreenPlugin implements IRutubePlayerPlugin {

    private IRutubePlayer rutubePlayer;
    private EndscreenView endscreenView;
    private IRutubePlayerPlugin plugin;

    public void activate(IRutubePlayer player) {
        player.addListener(listener);
        rutubePlayer = player;
        plugin = this;
    }

    public void deactivate(IRutubePlayer player) {
        rutubePlayer = null;
        plugin = null;
        player.removeListener(listener);
    }

    public boolean hasView() {return true;}
    public View getView(Context context) {

        if( endscreenView == null ) {
            endscreenView = new EndscreenView(context);
            endscreenView.endscreenListener = endscreenListener;
        }

        return endscreenView;
    }

    public boolean requestOperation( RutubePlayerOperation operation ) {return true;}

    private IRutubePlayerListener listener = new RutubePlayerListenerHelper() {

        @Override
        public void changeVideo() {
            if( endscreenView.isShown() )
                endscreenView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void changePlayerState(RutubePlayerState state) {

            switch( state ) {

                case FinishedState:
                    if( !endscreenView.isShown() )
                        endscreenView.setVisibility(View.VISIBLE);
                    break;

                default:
                    if( endscreenView.isShown() )
                        endscreenView.setVisibility(View.INVISIBLE);

            }

        }

    };

    private EndscreenView.IEndscreenViewListener endscreenListener = new EndscreenView.IEndscreenViewListener() {

        public void onReplayClick(){

            if( rutubePlayer != null )
                rutubePlayer.play(plugin);

        }

        public void onShareClick() {

        }

    };

}
