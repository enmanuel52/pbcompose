package com.enmanuelbergling.walkthrough.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.enmanuelbergling.walkthrough.common.DimenTokens
import com.enmanuelbergling.walkthrough.model.IndicatorStyle
import com.enmanuelbergling.walkthrough.model.StepStyle
import com.enmanuelbergling.walkthrough.model.WalkScrollStyle
import com.enmanuelbergling.walkthrough.model.WalkStep
import com.enmanuelbergling.walkthrough.ui.components.InstagramPager
import com.enmanuelbergling.walkthrough.ui.components.WalkThroughColors
import com.enmanuelbergling.walkthrough.ui.components.WalkThroughDefaults

/**
 * @param steps for every single page
 * @param colors for components
 * @param bottomButton button placed at the bottom of the walk
 * */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WalkThrough(
    steps: List<WalkStep>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    skipButton: @Composable () -> Unit = { },
    bottomButton: @Composable () -> Unit = {},
    colors: WalkThroughColors = WalkThroughDefaults.colors(),
    stepStyle: StepStyle = StepStyle.ImageUp,
    indicatorStyle: IndicatorStyle = IndicatorStyle.Step,
    scrollStyle: WalkScrollStyle = WalkScrollStyle.Normal,
) {
    if (scrollStyle == WalkScrollStyle.Instagram){
        InstagramPager(state = pagerState, modifier = modifier) {index, pageModifier->
            FilledWalkStepUi(
                step = steps[index],
                pagerState = pagerState,
                modifier = pageModifier,
                skipButton = skipButton,
                bottomButton = bottomButton,
                colors = colors,
                stepStyle = stepStyle,
                indicatorStyle = indicatorStyle,
            )
        }
    }
    else {
        ConstraintLayout(
            modifier = modifier
                .fillMaxSize()
                .background(colors.containerColor)
        ) {
            val (
                page,
                indicator,
                skipButtonRef,
                nextButton,
            ) = createRefs()

            val bottomContentTop = createGuidelineFromTop(.7f)

            HorizontalPager(
                state = pagerState, modifier = Modifier
                    .constrainAs(page) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(indicator.top, margin = DimenTokens.MediumSmall)
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints
                    },
                verticalAlignment = Alignment.Top
            ) { index ->
                WalkStepUi(
                    step = steps[index],
                    modifier = Modifier.fillMaxSize(),
                    stepStyle = stepStyle
                )
            }

            val indicatorModifier = Modifier
                .constrainAs(indicator) {
                    top.linkTo(bottomContentTop, margin = DimenTokens.Medium)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(nextButton.top)
                }

            when (indicatorStyle) {
                IndicatorStyle.Step -> {
                    StepIndicator(
                        pageIndex = pagerState.currentPage,
                        pageCount = pagerState.pageCount,
                        stepSize = DimenTokens.IndicatorSize,
                        modifier = indicatorModifier
                            .padding(DimenTokens.LessLarge),
                        colors = colors.indicator()
                    )
                }

                IndicatorStyle.Shift -> {
                    ShiftIndicator(
                        pagerState,
                        stepSize = DimenTokens.IndicatorSize,
                        modifier = indicatorModifier
                            .padding(DimenTokens.LessLarge),
                        colors = colors.indicator()
                    )
                }
            }

            if (pagerState.canScrollForward) {
                Box(modifier = Modifier
                    .constrainAs(
                        skipButtonRef
                    ) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    }
                    .padding(DimenTokens.Small)
                ) {
                    skipButton()
                }
            }

            Box(
                modifier = Modifier
                    .constrainAs(nextButton) {
                        top.linkTo(indicator.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
            ) {
                bottomButton()
            }
        }
    }
}

/**
 * All is on the page, even indicator and button
 * */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FilledWalkStepUi(
    step: WalkStep,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    skipButton: @Composable () -> Unit = { },
    bottomButton: @Composable () -> Unit = {},
    colors: WalkThroughColors = WalkThroughDefaults.colors(),
    stepStyle: StepStyle = StepStyle.ImageUp,
    indicatorStyle: IndicatorStyle = IndicatorStyle.Shift,
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(colors.containerColor)
    ) {
        val (
            page,
            indicator,
            skipButtonRef,
            nextButton,
        ) = createRefs()

        val bottomContentTop = createGuidelineFromTop(.7f)

        WalkStepUi(
            step = step,
            modifier = Modifier
                .constrainAs(page) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(indicator.top, margin = DimenTokens.MediumSmall)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                },
            stepStyle = stepStyle
        )

        val indicatorModifier = Modifier
            .constrainAs(indicator) {
                top.linkTo(bottomContentTop, margin = DimenTokens.Medium)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(nextButton.top)
            }

        when(indicatorStyle){
            IndicatorStyle.Step -> {
                StepIndicator(
                    pageIndex = pagerState.currentPage,
                    pageCount = pagerState.pageCount,
                    stepSize = DimenTokens.IndicatorSize,
                    modifier = indicatorModifier
                        .padding(DimenTokens.LessLarge),
                    colors = colors.indicator()
                )
            }
            IndicatorStyle.Shift ->{
                ShiftIndicator(
                    pagerState,
                    stepSize = DimenTokens.IndicatorSize,
                    modifier = indicatorModifier
                        .padding(DimenTokens.LessLarge),
                    colors = colors.indicator()
                )
            }
        }

        if (pagerState.canScrollForward) {
            Box(modifier = Modifier
                .constrainAs(
                    skipButtonRef
                ) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
                .padding(DimenTokens.Small)
            ) {
                skipButton()
            }
        }

        Box(
            modifier = Modifier
                .constrainAs(nextButton) {
                    top.linkTo(indicator.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
        ) {
            bottomButton()
        }
    }
}