package uk.droidcon.dina12.media;

import de.audi.auditablet.contract.remote.NowPlayingMediaContract;
import de.esolutions.applibs.provider.Config;

/**
 * Created by Arne Poths (mail@arnepoths.de) on 31/10/15.
 */
public interface Constants {

    boolean MOCK = Config.ENABLE_MOCK_PROVIDERS;

    String NOW_PLAYING_AUTHORITY = NowPlayingMediaContract.AUTHORITY + (MOCK ? "mock" : "");
}
