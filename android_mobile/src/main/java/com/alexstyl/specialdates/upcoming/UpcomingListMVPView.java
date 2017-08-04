package com.alexstyl.specialdates.upcoming;

import java.util.List;

public interface UpcomingListMVPView { // changed to public because of Kotlin complains

    void showLoading();

    void display(List<UpcomingRowViewModel> events);

    void showFirstEvent();

    boolean isEmpty();

    void askForContactPermission();
}
