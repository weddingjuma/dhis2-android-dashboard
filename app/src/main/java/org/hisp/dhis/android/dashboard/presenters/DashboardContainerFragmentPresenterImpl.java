/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dashboard.presenters;

import android.util.Log;

import org.hisp.dhis.android.dashboard.views.fragments.dashboard.DashboardContainerFragment;
import org.hisp.dhis.android.dashboard.views.fragments.dashboard.DashboardContainerFragmentView;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.android.api.utils.DefaultOnSubscribe;
import org.hisp.dhis.client.sdk.android.dashboard.DashboardInteractor;
import org.hisp.dhis.client.sdk.core.common.persistence.DbAction;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.ui.bindings.views.View;
import org.hisp.dhis.client.sdk.utils.Logger;

import java.util.EnumSet;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

// TODO Edit PresenterImpl Code
public class DashboardContainerFragmentPresenterImpl implements DashboardContainerFragmentPresenter {
    private static final String TAG = DashboardContainerFragmentPresenterImpl.class.getSimpleName();
    private final DashboardInteractor dashboardInteractor;
    private DashboardContainerFragmentView dashboardContainerFragmentView;
    private final Logger logger;

    private static final Boolean TEST_BOOL_EMPTY_DASHBOARD = false;
    private static final Boolean TEST_BOOL_VIEWPAGER = true;

    // TODO
    public DashboardContainerFragmentPresenterImpl(
            DashboardInteractor dashboardInteractor,Logger logger) {
        this.dashboardInteractor = dashboardInteractor;
        this.logger = logger;
    }

    public void attachView(View view) {
        isNull(view, "DashboardContainerFragmentView must not be null");
        if(dashboardContainerFragmentView==null) {
            dashboardContainerFragmentView = (DashboardContainerFragment) view;
        }
    }

    @Override
    public void detachView() {
        dashboardContainerFragmentView = null;
    }

    @Override
    public void onLoadLocalData() {
        logger.d(TAG, "onLoadLocalData()");
    }

    // TODO Replace by listByActions later
    private Observable<Boolean> checkIfSHasData() {
//        EnumSet<Action> updateActions = EnumSet.of(Action.TO_POST, Action.TO_UPDATE, Action.TO_DELETE);
//        return dashboardInteractor.listByActions(DbAction.INSERT)
        return dashboardInteractor.list()
                .switchMap(new Func1<List<Dashboard>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(final List<Dashboard> dashboards) {
                        return Observable.create(new DefaultOnSubscribe<Boolean>() {
                            @Override
                            public Boolean call() {
                                return dashboards != null && !dashboards.isEmpty();
                            }
                        });
                    }
                });
    }

    private void handleNavigation(Boolean hasData){
        if(hasData){
            // 2 Conditions :
            // if Empty fragment of container has to be loaded first, check for !=null
            // if onLoad() has to be done before loading empty fragment, do not check for !=null
            logger.d(TAG, "hasData");
            if (dashboardContainerFragmentView != null) {
                dashboardContainerFragmentView.navigationAfterLoadingData(TEST_BOOL_VIEWPAGER);
            }
        } else{
            logger.d(TAG , "hasNoData");
            if (dashboardContainerFragmentView != null) {
                dashboardContainerFragmentView.navigationAfterLoadingData(TEST_BOOL_EMPTY_DASHBOARD);
            }
        }
    }
}